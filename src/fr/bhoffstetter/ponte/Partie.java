package fr.bhoffstetter.ponte;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author Olivier
 *
 * gestion d'une partie
 *
 * aléatoire
 *
 */
public class Partie {

    static int nbLignesColonnes; // dimension (carrée) du plateau

    private static Random rnd = new Random();

    public final static char carPose2Pions = '+'; // caractère de pose de 2 pions
    public final static char carPose1Pont = '-'; // caractère de pose d'1 pont
    public final static char carArret = 'a'; // caractère d'arrêt

    private InputStream fluxEntree; // flux en entrée depuis le serveur
    private OutputStream fluxSortie; // flux en sortie vers le serveur

    @SuppressWarnings("unused")
    private boolean estPremierJoueur; // indique si le joueur joue en premier (ou en second sinon)
    private boolean couleurAChoisir; // indique si la couleur des pions est encore à choisir (en début de partie, au 2e coup) lorsque l'on est le second joueur
    private boolean couleurCLAIR; // couleur des pions

    //Tableau qui stocke le plateau
    private int tableauJeu[][];

    /**
     * gestion d'une partie
     * @param fluxEntree
     * @param fluxSortie
     * @param nbLignesColonnes
     */
    public Partie(InputStream fluxEntree, OutputStream fluxSortie, int nbLignesColonnes) {
        this.fluxEntree = fluxEntree;
        this.fluxSortie = fluxSortie;
        Partie.nbLignesColonnes = nbLignesColonnes;
        tableauJeu = new int[nbLignesColonnes][nbLignesColonnes];
    }
    /**
     * gestion d'une partie
     */
    public void partie() {
        boolean fini = false;
        while (! fini) {
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
                    if (! couleurCLAIR) {
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
     * @param codeRetourArbitre
     * @return indique si ça s'est bien passé
     */
    //TODO gérer réception code de retour arbitre
    private boolean receptionAction(int codeRetourArbitre) {
        if (reception1CodeRetourArbitre() == -1) {
            return false;
        }
        if (reception1CodeRetourArbitre() == -1) {
            return false;
        }
        if (reception1CodeRetourArbitre() == -1) {
            return false;
        }
        if (reception1CodeRetourArbitre() == -1) {
            return false;
        }
        return true;
    }

    /**
     * envoi de l'action au serveur
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
     * @return action
     */
    //TODO Implémenter IA
    private String action() {
        if (Math.random() < 0.05) {
            return Character.toString(carArret);
        } else {
            return Integer.toString(rnd.nextInt(nbLignesColonnes)) + Integer.toString(rnd.nextInt(nbLignesColonnes)) + Character.toString((Math.random()<0.6?carPose2Pions:carPose1Pont)) + Integer.toString(rnd.nextInt(nbLignesColonnes)) + Integer.toString(rnd.nextInt(nbLignesColonnes));
        }
    }

}


