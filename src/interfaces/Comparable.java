package interfaces;

import models.Article;
import models.ResultatComparaison;
import java.util.List;

/**
 * Interface pour la comparaison entre deux auteurs scientifiques.
 * NOUVELLE INTERFACE ajoutée pour la fonctionnalité de comparaison.
 */
public interface Comparable {

    /**
     * Compare deux auteurs et retourne un résultat de comparaison
     */
    ResultatComparaison comparerAuteurs(String auteur1, String auteur2,
                                        List<Article> articles1, List<Article> articles2);

    /**
     * Retourne le nombre total de publications d'un auteur
     */
    int getNombrePublications(List<Article> articles);

    /**
     * Retourne le h-index estimé d'un auteur
     */
    int calculerHIndex(List<Article> articles);

    /**
     * Retourne le total des citations d'un auteur
     */
    int getTotalCitations(List<Article> articles);
}
