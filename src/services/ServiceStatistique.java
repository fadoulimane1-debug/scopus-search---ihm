package services;

import interfaces.Statistique;
import models.Article;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de calcul des statistiques.
 * Implémente l'interface Statistique.
 */
public class ServiceStatistique implements Statistique {

    @Override
    public int calculerTotalCitations(List<Article> articles) {
        return articles.stream().mapToInt(Article::getCitations).sum();
    }

    @Override
    public double calculerMoyenneCitations(List<Article> articles) {
        if (articles.isEmpty()) return 0;
        return articles.stream().mapToInt(Article::getCitations).average().orElse(0);
    }

    @Override
    public Map<String, Integer> compterParDomaine(List<Article> articles) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Article a : articles) {
            map.merge(a.getDomaine(), 1, Integer::sum);
        }
        // Trier par valeur décroissante
        return map.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public Map<Integer, Integer> compterParAnnee(List<Article> articles) {
        Map<Integer, Integer> map = new TreeMap<>();
        for (Article a : articles) {
            map.merge(a.getAnnee(), 1, Integer::sum);
        }
        return map;
    }

    @Override
    public Article getArticlePlusCite(List<Article> articles) {
        return articles.stream()
            .max(Comparator.comparingInt(Article::getCitations))
            .orElse(null);
    }

    @Override
    public double getPourcentageOpenAccess(List<Article> articles) {
        if (articles.isEmpty()) return 0;
        long oa = articles.stream().filter(Article::isOpenAccess).count();
        return (double) oa / articles.size() * 100;
    }
}
