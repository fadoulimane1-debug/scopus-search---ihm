package services;

import interfaces.Recherchable;
import models.Article;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de recherche utilisant la vraie API Scopus.
 * Implémente l'interface Recherchable.
 */
public class ServiceRecherche implements Recherchable {

    // ⚠️ REMPLACE PAR TA VRAIE CLÉ API
    private static final String API_KEY  = "793d48015988610ff0bb2494d7adb11c";
    private static final String BASE_URL = "https://api.elsevier.com/content/search/scopus";

    private List<Article> dernierResultat = new ArrayList<>();

    @Override
    public List<Article> rechercher(String motCle) {
        return appelAPI("TITLE-ABS-KEY(" + motCle + ")");
    }

    @Override
    public List<Article> rechercherParAuteur(String auteur) {
        return appelAPI("AUTHOR-NAME(" + auteur + ")");
    }

    @Override
    public List<Article> rechercherParAnnee(int annee) {
        return appelAPI("PUBYEAR = " + annee);
    }

    @Override
    public List<Article> rechercherParDomaine(String domaine) {
        return appelAPI("SUBJAREA(" + domaine + ")");
    }

    @Override
    public List<Article> rechercheAvancee(String motCle, String auteur,
                                           int anneeMin, int anneeMax, String domaine) {
        StringBuilder query = new StringBuilder();
        if (!motCle.isEmpty())  append(query, "TITLE-ABS-KEY(" + motCle + ")");
        if (!auteur.isEmpty())  append(query, "AUTHOR-NAME(" + auteur + ")");
        if (anneeMin > 0)       append(query, "PUBYEAR > " + (anneeMin - 1));
        if (anneeMax > 0)       append(query, "PUBYEAR < " + (anneeMax + 1));
        if (!domaine.isEmpty()) append(query, "SUBJAREA(" + domaine + ")");
        if (query.length() == 0) query.append("TITLE-ABS-KEY(computer science)");
        return appelAPI(query.toString());
    }

    private void append(StringBuilder sb, String clause) {
        if (sb.length() > 0) sb.append(" AND ");
        sb.append(clause);
    }

    private List<Article> appelAPI(String query) {
        List<Article> articles = new ArrayList<>();
        try {
            String enc = URLEncoder.encode(query, "UTF-8");
            String urlStr = BASE_URL + "?query=" + enc
                + "&count=25&field=dc:title,dc:creator,prism:coverDate,"
                + "prism:publicationName,citedby-count,dc:description,openaccess,prism:doi,subject-area";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-ELS-APIKey", API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                articles = parserJSON(sb.toString());
                dernierResultat = articles;
            }
            conn.disconnect();
        } catch (Exception e) {
            System.err.println("Erreur API : " + e.getMessage());
        }
        return articles;
    }

    private List<Article> parserJSON(String json) {
        List<Article> articles = new ArrayList<>();
        try {
            org.json.JSONObject root    = new org.json.JSONObject(json);
            org.json.JSONObject results = root.getJSONObject("search-results");
            org.json.JSONArray  entries = results.optJSONArray("entry");
            if (entries == null) return articles;

            for (int i = 0; i < entries.length(); i++) {
                org.json.JSONObject e = entries.getJSONObject(i);
                String titre   = e.optString("dc:title",              "Titre inconnu");
                String auteur  = e.optString("dc:creator",            "Auteur inconnu");
                String journal = e.optString("prism:publicationName", "Journal inconnu");
                String doi     = e.optString("prism:doi",             "N/A-" + i);
                String resume  = e.optString("dc:description",        "Résumé non disponible.");
                String dateStr = e.optString("prism:coverDate",       "2000-01-01");
                int annee = 2000;
                try { annee = Integer.parseInt(dateStr.substring(0, 4)); } catch (Exception ignored) {}
                int citations = 0;
                try { citations = Integer.parseInt(e.optString("citedby-count", "0")); } catch (Exception ignored) {}
                boolean oa = "1".equals(e.optString("openaccess", "0"));
                String domaine = "Sciences";
                org.json.JSONArray subs = e.optJSONArray("subject-area");
                if (subs != null && subs.length() > 0)
                    domaine = subs.getJSONObject(0).optString("$", "Sciences");

                articles.add(new Article(titre, auteur, annee, domaine, citations, journal, resume, oa, doi));
            }
        } catch (Exception e) {
            System.err.println("Erreur parsing : " + e.getMessage());
        }
        return articles;
    }

    public List<Article> getTousLesArticles() {
        if (dernierResultat.isEmpty())
            return appelAPI("TITLE-ABS-KEY(artificial intelligence)");
        return new ArrayList<>(dernierResultat);
    }

    public List<Article> getDernierResultat() { return dernierResultat; }
}
