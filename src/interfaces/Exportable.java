package interfaces;

import models.Article;
import java.util.List;

/**
 * Interface pour l'export des résultats de recherche.
 */
public interface Exportable {
    void exporterCSV(List<Article> articles, String cheminFichier);
    void exporterTXT(List<Article> articles, String cheminFichier);
    String genererRapport(List<Article> articles);
}
