package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe représentant une requête de recherche dans l'historique.
 */
public class RequeteRecherche {
    private String motCle;
    private String auteur;
    private int nbResultats;
    private LocalDateTime dateHeure;

    public RequeteRecherche(String motCle, String auteur, int nbResultats) {
        this.motCle = motCle;
        this.auteur = auteur;
        this.nbResultats = nbResultats;
        this.dateHeure = LocalDateTime.now();
    }

    public String getMotCle()      { return motCle; }
    public String getAuteur()      { return auteur; }
    public int getNbResultats()    { return nbResultats; }
    public String getDateHeure() {
        return dateHeure.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }

    @Override
    public String toString() {
        String q = !motCle.isEmpty() ? motCle : (!auteur.isEmpty() ? auteur : "—");
        return "[" + getDateHeure() + "] " + q + " → " + nbResultats + " résultats";
    }
}
