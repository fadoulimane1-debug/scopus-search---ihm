package interfaces;

import models.Article;
import java.util.List;

/**
 * Interface pour le filtrage des résultats.
 */
public interface Filtrable {
    List<Article> filtrerParAnnee(List<Article> articles, int anneeMin, int anneeMax);
    List<Article> filtrerParCitations(List<Article> articles, int citationsMin);
    List<Article> filtrerParDomaine(List<Article> articles, String domaine);
    List<Article> filtrerOpenAccess(List<Article> articles);
}
