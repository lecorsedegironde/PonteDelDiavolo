package fr.bhoffstetter.ponte;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author Olivier
 *
 * "IA" aléatoire pour tester les applications des joueurs
 *
 * <cde> <adresse du serveur> <port du serveur> <dimension du plateau>
 */
public class IABastien {

    private IABastien() {}
    /**
     * "IA" pour tester les applications des joueurs
     * @param args = adresseServeur portServeur dimensionPlateau
     */
    public static void main(String[] args) {
        InetAddress adresseHoteServeur; // adresse du serveur (c.-à-d. l'arbitre)
        int portServeur = -1; // port du serveur (c.-à-d. l'arbitre) dédié à cette application
        int nbLignesColonnes = -1; // dimension (carrée) du plateau
        ClientTCP client;
        System.out.println("main joueur : démarrage à " + (new Date()));
        adresseHoteServeur = null;
        try {
            adresseHoteServeur = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println("main joueur : erreur car l'adresse de l'hôte n'est pas correcte ; "+e);
            e.printStackTrace();
        }
        try {
            portServeur = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("main joueur : erreur car le port (2d argument) n'est pas un nombre ; "+e);
            e.printStackTrace();
        }
        try {
            nbLignesColonnes = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("main joueur : erreur car la dimension (carrée) du plateau (3e argument) n'est pas un nombre ; "+e);
            e.printStackTrace();
        }
        System.out.println("main joueur : démarré sur le port "+portServeur+" à "+ (new Date()));
        client = new ClientTCP(adresseHoteServeur, portServeur, nbLignesColonnes); // lien avec le serveur
        client.lancementClientTCP();
        System.out.println("main joueur : arrêt à " + (new Date()));
    }

}


