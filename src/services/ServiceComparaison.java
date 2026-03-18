package services;

import interfaces.Comparable;
import models.Article;
import models.ResultatComparaison;
import java.util.List;

/**
 * Service de comparaison entre deux auteurs.
 * Implémente l'interface Comparable.
 */
public class ServiceComparaison implements Comparable {

    @Override
    public ResultatComparaison comparerAuteurs(String auteur1, String auteur2,
                                               List<Article> articles1, List<Article> articles2) {
        ResultatComparaison res = new ResultatComparaison(auteur1, auteur2);

        res.setPublications1(getNombrePublications(articles1));
        res.setPublications2(getNombrePublications(articles2));
        res.setCitations1(getTotalCitations(articles1));
        res.setCitations2(getTotalCitations(articles2));
        res.setHIndex1(calculerHIndex(articles1));
        res.setHIndex2(calculerHIndex(articles2));

        double moy1 = articles1.isEmpty() ? 0 :
            articles1.stream().mapToInt(Article::getCitations).average().orElse(0);
        double moy2 = articles2.isEmpty() ? 0 :
            articles2.stream().mapToInt(Article::getCitations).average().orElse(0);
        res.setMoyenneCitations1(moy1);
        res.setMoyenneCitations2(moy2);

        return res;
    }

    @Override
    public int getNombrePublications(List<Article> articles) {
        return articles.size();
    }

    @Override
    public int calculerHIndex(List<Article> articles) {
        // H-index : h articles ont au moins h citations
        List<Integer> citations = articles.stream()
            .map(Article::getCitations)
            .sorted(java.util.Comparator.reverseOrder())
            .collect(java.util.stream.Collectors.toList());

        int h = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }

    @Override
    public int getTotalCitations(List<Article> articles) {
        return articles.stream().mapToInt(Article::getCitations).sum();
    }
}
