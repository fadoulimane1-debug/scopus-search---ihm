package interfaces;

import models.Article;
import java.util.List;

/**
 * Interface pour la sauvegarde et le chargement des favoris.
 */
public interface Sauvegardable {
    void sauvegarder(Article article);
    void supprimer(Article article);
    List<Article> charger();
    boolean estSauvegarde(Article article);
}
