package services;

import interfaces.Filtrable;
import models.Article;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de filtrage. Implémente Filtrable.
 */
public class ServiceFiltre implements Filtrable {
    @Override
    public List<Article> filtrerParAnnee(List<Article> articles, int anneeMin, int anneeMax) {
        return articles.stream().filter(a -> a.getAnnee() >= anneeMin && a.getAnnee() <= anneeMax).collect(Collectors.toList());
    }
    @Override
    public List<Article> filtrerParCitations(List<Article> articles, int citationsMin) {
        return articles.stream().filter(a -> a.getCitations() >= citationsMin).collect(Collectors.toList());
    }
    @Override
    public List<Article> filtrerParDomaine(List<Article> articles, String domaine) {
        return articles.stream().filter(a -> a.getDomaine().equalsIgnoreCase(domaine)).collect(Collectors.toList());
    }
    @Override
    public List<Article> filtrerOpenAccess(List<Article> articles) {
        return articles.stream().filter(Article::isOpenAccess).collect(Collectors.toList());
    }
}
