package services;

import interfaces.Exportable;
import models.Article;
import java.io.*;
import java.util.List;

/**
 * Service d'export. Implémente Exportable.
 */
public class ServiceExport implements Exportable {

    @Override
    public void exporterCSV(List<Article> articles, String cheminFichier) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(cheminFichier))) {
            pw.println("Titre,Auteur,Année,Domaine,Citations,Journal,Open Access,DOI");
            for (Article a : articles) {
                pw.printf("\"%s\",\"%s\",%d,\"%s\",%d,\"%s\",%s,\"%s\"%n",
                    a.getTitre(), a.getAuteur(), a.getAnnee(), a.getDomaine(),
                    a.getCitations(), a.getJournal(), a.isOpenAccess() ? "Oui" : "Non", a.getDoi());
            }
        } catch (IOException e) { System.err.println("Erreur CSV : " + e.getMessage()); }
    }

    @Override
    public void exporterTXT(List<Article> articles, String cheminFichier) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(cheminFichier))) {
            pw.println("=== RÉSULTATS SCOPUS SEARCH ===");
            pw.println("Nombre d'articles : " + articles.size());
            pw.println("================================\n");
            int i = 1;
            for (Article a : articles) {
                pw.println(i++ + ". " + a.getTitre());
                pw.println("   Auteur   : " + a.getAuteur());
                pw.println("   Année    : " + a.getAnnee());
                pw.println("   Journal  : " + a.getJournal());
                pw.println("   Citations: " + a.getCitations());
                pw.println("   DOI      : " + a.getDoi());
                pw.println("   Résumé   : " + a.getResume());
                pw.println();
            }
        } catch (IOException e) { System.err.println("Erreur TXT : " + e.getMessage()); }
    }

    @Override
    public String genererRapport(List<Article> articles) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPPORT SCOPUS SEARCH ===\n");
        sb.append("Total articles   : ").append(articles.size()).append("\n");
        int total = articles.stream().mapToInt(Article::getCitations).sum();
        long oa   = articles.stream().filter(Article::isOpenAccess).count();
        sb.append("Total citations  : ").append(total).append("\n");
        sb.append("Open Access      : ").append(oa).append("\n\n");
        for (int i = 0; i < articles.size(); i++) {
            Article a = articles.get(i);
            sb.append((i+1)).append(". ").append(a.getTitre()).append("\n");
            sb.append("   ").append(a.getAuteur()).append(" | ").append(a.getAnnee())
              .append(" | ").append(a.getCitations()).append(" citations\n\n");
        }
        return sb.toString();
    }
}
