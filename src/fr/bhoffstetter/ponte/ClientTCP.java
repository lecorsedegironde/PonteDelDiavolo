package fr.bhoffstetter.ponte;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author Olivier
 *
 * connexion et déconnexion au serveur (c.-à-d. l'arbitre), et lancement d'une partie
 *
 */
public class ClientTCP {

    private InetAddress adresseHoteServeur; // adresse du serveur (c.-à-d. l'arbitre)
    private int portServeur; // port du serveur (c.-à-d. l'arbitre) dédié à cette application
    private int nbLignesColonnes; // dimension (carrée) du plateau

    /**
     * connexion et déconnexion au serveur (c.-à-d. l'arbitre), et gestion d'une partie
     * @param adresseHoteServeur
     * @param portServeur
     */
    public ClientTCP(InetAddress adresseHoteServeur, int portServeur, int nbLignesColonnes) {
        this.adresseHoteServeur = adresseHoteServeur;
        this.portServeur = portServeur;
        this.nbLignesColonnes = nbLignesColonnes;
    }

    /**
     * lancement du client TCP
     */
    public void lancementClientTCP() {
        System.out.println("Client : démarrage sur le port "+portServeur+" à "+ (new Date()));
        Socket s; // socket permettant d'accéder au serveur
        InputStream fluxEntree; // flux en entrée
        OutputStream fluxSortie; // flux en sortie
        try {
            s = null; // socket pour la connexion au serveur
            try {
                s = new Socket(adresseHoteServeur, portServeur); // connexion au serveur
            } catch (Exception e) {
                System.out.println("Client : erreur de connexion au serveur "+e);
                e.printStackTrace();
            }
            fluxEntree = null;
            try {
                fluxEntree = s.getInputStream();
            } catch (IOException e) {
                System.out.println("Client : erreur pour le flux en entrée "+e);
                e.printStackTrace();
            }
            fluxSortie = null;
            try {
                fluxSortie = s.getOutputStream();
            } catch (IOException e) {
                System.out.println("Client : erreur pour le flux en sortie "+e);
                e.printStackTrace();
            }
            if (fluxEntree != null && fluxSortie != null) {
                Partie partie = new Partie(fluxEntree, fluxSortie, nbLignesColonnes); // partie
                partie.partie();
            } else {
                System.out.println("Client : pas de flux en entrée ou en sortie");
            }
            if (fluxEntree != null) {
                fluxEntree.close();
            }
            if (fluxSortie != null) {
                fluxSortie.close();
            }
            if (s != null) {
                s.close(); // déconnexion du serveur
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client : arrêt à " + (new Date()));
    }

}


