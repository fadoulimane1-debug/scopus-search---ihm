package interfaces;

import models.Article;
import java.util.List;

/**
 * Interface pour le tri des articles scientifiques.
 */
public interface Triable {
    List<Article> trierParCitations(List<Article> articles);
    List<Article> trierParDate(List<Article> articles);
    List<Article> trierParTitre(List<Article> articles);
    List<Article> trierParAuteur(List<Article> articles);
}
