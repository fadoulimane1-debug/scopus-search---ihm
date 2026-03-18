package interfaces;

import models.Article;
import java.util.List;
import java.util.Map;

/**
 * Interface pour le calcul et l'affichage des statistiques.
 * NOUVELLE INTERFACE ajoutée pour les graphiques et analyses.
 */
public interface Statistique {

    /**
     * Calcule le total des citations d'une liste d'articles
     */
    int calculerTotalCitations(List<Article> articles);

    /**
     * Calcule la moyenne des citations
     */
    double calculerMoyenneCitations(List<Article> articles);

    /**
     * Retourne le nombre d'articles par domaine : domaine → count
     */
    Map<String, Integer> compterParDomaine(List<Article> articles);

    /**
     * Retourne le nombre d'articles par année : année → count
     */
    Map<Integer, Integer> compterParAnnee(List<Article> articles);

    /**
     * Retourne l'article le plus cité
     */
    Article getArticlePlusCite(List<Article> articles);

    /**
     * Retourne le pourcentage d'articles en open access
     */
    double getPourcentageOpenAccess(List<Article> articles);
}
