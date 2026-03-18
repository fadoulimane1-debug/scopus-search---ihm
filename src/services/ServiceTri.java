package services;

import interfaces.Triable;
import models.Article;
import java.util.*;

/**
 * Service de tri. Implémente Triable.
 */
public class ServiceTri implements Triable {
    @Override
    public List<Article> trierParCitations(List<Article> articles) {
        List<Article> l = new ArrayList<>(articles);
        l.sort(Comparator.comparingInt(Article::getCitations).reversed());
        return l;
    }
    @Override
    public List<Article> trierParDate(List<Article> articles) {
        List<Article> l = new ArrayList<>(articles);
        l.sort(Comparator.comparingInt(Article::getAnnee).reversed());
        return l;
    }
    @Override
    public List<Article> trierParTitre(List<Article> articles) {
        List<Article> l = new ArrayList<>(articles);
        l.sort(Comparator.comparing(Article::getTitre));
        return l;
    }
    @Override
    public List<Article> trierParAuteur(List<Article> articles) {
        List<Article> l = new ArrayList<>(articles);
        l.sort(Comparator.comparing(Article::getAuteur));
        return l;
    }
}
