package interfaces;

import models.RequeteRecherche;
import java.util.List;

/**
 * Interface pour la gestion de l'historique des recherches.
 * NOUVELLE INTERFACE pour tracker les recherches effectuées.
 */
public interface Historique {

    /**
     * Ajoute une requête dans l'historique
     */
    void ajouterRequete(RequeteRecherche requete);

    /**
     * Retourne tout l'historique de recherches
     */
    List<RequeteRecherche> getHistorique();

    /**
     * Efface tout l'historique
     */
    void effacerHistorique();

    /**
     * Retourne les N dernières recherches
     */
    List<RequeteRecherche> getDernieresRecherches(int n);

    /**
     * Retourne les recherches les plus fréquentes
     */
    List<String> getRecherchesFrequentes();
}
