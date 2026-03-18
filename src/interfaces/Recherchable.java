package interfaces;

import models.Article;
import java.util.List;

/**
 * Interface définissant les opérations de recherche d'articles scientifiques.
 */
public interface Recherchable {
    List<Article> rechercher(String motCle);
    List<Article> rechercherParAuteur(String auteur);
    List<Article> rechercherParAnnee(int annee);
    List<Article> rechercherParDomaine(String domaine);
    List<Article> rechercheAvancee(String motCle, String auteur, int anneeMin, int anneeMax, String domaine);
}
