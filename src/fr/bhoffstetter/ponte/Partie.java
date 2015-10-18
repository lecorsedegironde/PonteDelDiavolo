package fr.bhoffstetter.ponte;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author Olivier
 *         <p>
 *         gestion d'une partie
 *         <p>
 *         aléatoire
 */
public class Partie {

    public final static char carPose2Pions = '+'; // caractère de pose de 2 pions
    public final static char carPose1Pont = '-'; // caractère de pose d'1 pont
    public final static char carArret = 'a'; // caractère d'arrêt
    static int nbLignesColonnes; // dimension (carrée) du plateau
    private static Random rnd = new Random();
    private InputStream fluxEntree; // flux en entrée depuis le serveur
    private OutputStream fluxSortie; // flux en sortie vers le serveur


    private boolean estPremierJoueur; // indique si le joueur joue en premier (ou en second sinon)
    private boolean couleurAChoisir; // indique si la couleur des pions est encore à choisir (en début de partie, au 2e coup) lorsque l'on est le second joueur
    private boolean couleurCLAIR; // couleur des pions

    //Tableau qui stocke le plateau --> 0 si clair, 1 si foncé, 2 si rien, si il y a un pont : LCcouleurPion et 3 si espace bloqué
    private int tableauJeu[][];
    //private

    /**
     * gestion d'une partie
     *
     * @param fluxEntree
     * @param fluxSortie
     * @param nbLignesColonnes
     */
    public Partie(InputStream fluxEntree, OutputStream fluxSortie, int nbLignesColonnes) {
        this.fluxEntree = fluxEntree;
        this.fluxSortie = fluxSortie;
        Partie.nbLignesColonnes = nbLignesColonnes;
        tableauJeu = new int[nbLignesColonnes][nbLignesColonnes];
        for (int i = 0; i < nbLignesColonnes; i++) {
            for (int j = 0; j < nbLignesColonnes; j++) {
                tableauJeu[i][j] = 2;
            }
        }
    }

    /**
     * gestion d'une partie
     */
    public void partie() {
        boolean fini = false;
        while (!fini) {
            int codeRetourArbitre = reception1CodeRetourArbitre();
            switch (codeRetourArbitre) {
                case (int) 'P':
                    // on est le premier joueur et on débute avec les pions clairs, et on joue le 1er coup (on commence la partie)
                    estPremierJoueur = true;
                    couleurCLAIR = true;
                    couleurAChoisir = false;
                    envoiAction(action());
                    break;
                case (int) 'S':
                    // on est le second joueur et on débute avec les pions foncés
                    estPremierJoueur = false;
                    couleurCLAIR = false;
                    couleurAChoisir = true;
                    break;
                case (int) 'c':
                    // (on est donc le premier joueur avec les pions clairs et) le second joueur vient de décider de jouer avec les pions clairs (c.-à-d. d'inverser les couleurs initiales), et on joue le 2e coup
                    couleurCLAIR = false;
                    // choixCouleurAEffectuer = true;
                    envoiAction(action());
                    break;
                case (int) 'f':
                    // (on est donc le premier joueur avec les pions clairs et) le second joueur vient de décider de jouer les pions foncés (c.-à-d. de garder les couleurs initiales)
                    // choixCouleurAEffectuer = true;
                    break;
                case (int) carArret:
                    // notre adversaire arrête ; c'est notre dernier coup si on a les pions foncés
                    if (!couleurCLAIR) {
                        envoiAction(action());
                    }
                    break;
                case (int) 'F':
                    // fin de la partie
                    fini = true;
                    break;
                case -1:
                    System.out.println("Client : erreur du flux en entrée");
                    fini = true;
                    break;
                default:
                    if (receptionAction(codeRetourArbitre)) {
                        if (couleurAChoisir) { // (on est donc le second joueur avec les pions foncés et c'est le début de la partie) on doit choisir la couleur des pions avec lesquels on veut jouer dorénavant
                            couleurAChoisir = false;
                            //TODO Choisir avec quelle couleur jouer
                            if (rnd.nextBoolean()) {
                                couleurCLAIR = true;
                                envoiAction("c"); // envoi du choix de la couleur
                            } else {
                                // couleurCLAIR = false;
                                envoiAction("f"); // envoi du choix de la couleur
                                envoiAction(action()); // on enchaîne par l'action du 2e coup du second joueur jouant avec les pions foncés
                            }
                        } else {
                            envoiAction(action());
                        }
                    } else {
                        System.out.println("Client : erreur du flux en entrée (... ou de l'arbitre)");
                        fini = true;
                        break;
                    }
                    break;
            }
        }
    }


    /**
     * réception d'un code du serveur
     *
     * @return codeRetourArbitre
     */
    private int reception1CodeRetourArbitre() {
        int codeRetourArbitre = -1;
        try {
            codeRetourArbitre = fluxEntree.read();
        } catch (IOException ioe) {
            System.out.println("Erreur en réception du serveur vers le client : " + ioe);
            ioe.printStackTrace();
        }
        return codeRetourArbitre;
    }

    /**
     * réception de l'action de l'adversaire
     *
     * @param codeRetourArbitre La première ligne
     * @return indique si ça s'est bien passé
     */
    //TODO gérer réception code de retour arbitre
    private boolean receptionAction(int codeRetourArbitre) {
        int L1 = codeRetourArbitre, C1, L2, C2;
        char played;

        //Récupération première colone
        C1 = reception1CodeRetourArbitre();
        if (C1 == -1) {
            return false;
        }
        //Récupération pion ou pont
        played = (char) reception1CodeRetourArbitre();
        if (reception1CodeRetourArbitre() == -1) {
            return false;
        }
        //Réception deuxième ligne
        L2 = reception1CodeRetourArbitre();
        if (L2 == -1) {
            return false;
        }
        //Réception deuxième colone
        C2 = reception1CodeRetourArbitre();
        if (C2 == -1) {
            return false;
        }

        //On stocke l'action éffectuée par l'adversaire
        if (played == carPose2Pions) {
            tableauJeu[L1][C1] = couleurCLAIR ? 0 : 1;
        } else if (played == carPose1Pont) {
            tableauJeu[L1][C1] = L2 * 100 + C2 * 10 + (couleurCLAIR ? 0 : 1);
            tableauJeu[L2][C2] = L1 * 100 + C1 * 10 + (couleurCLAIR ? 0 : 1);
            bloquerCasesPont(L1, C1, L2, C2);
        } else {
            return false;
        }

        return true;
    }

    /**
     * envoi de l'action au serveur
     *
     * @param action
     */
    private void envoiAction(String action) {
        for (int i = 0; i < action.length(); i++) {
            try {
                fluxSortie.write((int) action.charAt(i));
            } catch (IOException ioe) {
                System.out.println("Erreur en émission (write) du client vers le serveur : " + ioe);
                ioe.printStackTrace();
            }
        }
    }

    /**
     * action (LC+LC ou LC-LC voire arrêt)
     *
     * @return action
     */
    //TODO Implémenter IA
    private String action() {
        if (Math.random() < 0.05) {
            return Character.toString(carArret);
        } else {
            return Integer.toString(rnd.nextInt(nbLignesColonnes)) + Integer.toString(rnd.nextInt(nbLignesColonnes)) + Character.toString((Math.random() < 0.6 ? carPose2Pions : carPose1Pont)) + Integer.toString(rnd.nextInt(nbLignesColonnes)) + Integer.toString(rnd.nextInt(nbLignesColonnes));
        }
    }

    private void bloquerCasesPont(int L1, int C1, int L2, int C2) {
        if (L1 == L2) {
            if (C1 < C2) tableauJeu[L1][C1 + 1] = 3;
            else tableauJeu[L1][C1 - 1] = 3;
        } else if (C1 == C2) {
            if (L1 < L2) tableauJeu[L1 + 1][C1] = 3;
            else tableauJeu[L1 - 1][C1] = 3;
        } else if (L1 > L2) {
            //if ()
        } else if (L1 < L2) {

        }

    }
}


