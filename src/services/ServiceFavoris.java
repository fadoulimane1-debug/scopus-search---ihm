package services;

import interfaces.Sauvegardable;
import models.Article;
import java.util.ArrayList;
import java.util.List;

/**
 * Service des favoris. Implémente Sauvegardable.
 */
public class ServiceFavoris implements Sauvegardable {

    private List<Article> favoris = new ArrayList<>();

    @Override
    public void sauvegarder(Article article) {
        if (!estSauvegarde(article)) favoris.add(article);
    }

    @Override
    public void supprimer(Article article) {
        favoris.removeIf(a -> a.getDoi().equals(article.getDoi()));
    }

    @Override
    public List<Article> charger() { return new ArrayList<>(favoris); }

    @Override
    public boolean estSauvegarde(Article article) {
        return favoris.stream().anyMatch(a -> a.getDoi().equals(article.getDoi()));
    }

    public int getNombreFavoris() { return favoris.size(); }
}
