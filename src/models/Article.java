package models;

/**
 * Classe représentant un article scientifique Scopus.
 */
public class Article {
    private String titre;
    private String auteur;
    private int annee;
    private String domaine;
    private int citations;
    private String journal;
    private String resume;
    private boolean openAccess;
    private String doi;

    public Article(String titre, String auteur, int annee, String domaine,
                   int citations, String journal, String resume, boolean openAccess, String doi) {
        this.titre = titre; this.auteur = auteur; this.annee = annee;
        this.domaine = domaine; this.citations = citations; this.journal = journal;
        this.resume = resume; this.openAccess = openAccess; this.doi = doi;
    }

    public String getTitre()      { return titre; }
    public String getAuteur()     { return auteur; }
    public int    getAnnee()      { return annee; }
    public String getDomaine()    { return domaine; }
    public int    getCitations()  { return citations; }
    public String getJournal()    { return journal; }
    public String getResume()     { return resume; }
    public boolean isOpenAccess() { return openAccess; }
    public String getDoi()        { return doi; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Article)) return false;
        return doi.equals(((Article) obj).doi);
    }
    @Override public int hashCode() { return doi.hashCode(); }
    @Override public String toString() {
        return "[" + annee + "] " + titre + " — " + auteur + " (" + citations + " cit.)";
    }
}
