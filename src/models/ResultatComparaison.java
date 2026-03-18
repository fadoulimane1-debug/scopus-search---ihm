package models;

/**
 * Classe représentant le résultat d'une comparaison entre deux auteurs.
 */
public class ResultatComparaison {
    private String auteur1;
    private String auteur2;
    private int publications1;
    private int publications2;
    private int citations1;
    private int citations2;
    private int hIndex1;
    private int hIndex2;
    private double moyenneCitations1;
    private double moyenneCitations2;

    public ResultatComparaison(String auteur1, String auteur2) {
        this.auteur1 = auteur1;
        this.auteur2 = auteur2;
    }

    // Getters & Setters
    public String getAuteur1()              { return auteur1; }
    public String getAuteur2()              { return auteur2; }
    public int getPublications1()           { return publications1; }
    public int getPublications2()           { return publications2; }
    public int getCitations1()              { return citations1; }
    public int getCitations2()              { return citations2; }
    public int getHIndex1()                 { return hIndex1; }
    public int getHIndex2()                 { return hIndex2; }
    public double getMoyenneCitations1()    { return moyenneCitations1; }
    public double getMoyenneCitations2()    { return moyenneCitations2; }

    public void setPublications1(int p)     { this.publications1 = p; }
    public void setPublications2(int p)     { this.publications2 = p; }
    public void setCitations1(int c)        { this.citations1 = c; }
    public void setCitations2(int c)        { this.citations2 = c; }
    public void setHIndex1(int h)           { this.hIndex1 = h; }
    public void setHIndex2(int h)           { this.hIndex2 = h; }
    public void setMoyenneCitations1(double m) { this.moyenneCitations1 = m; }
    public void setMoyenneCitations2(double m) { this.moyenneCitations2 = m; }

    public String getGagnant() {
        int score1 = 0, score2 = 0;
        if (publications1 > publications2) score1++; else score2++;
        if (citations1    > citations2)    score1++; else score2++;
        if (hIndex1       > hIndex2)       score1++; else score2++;
        return score1 >= score2 ? auteur1 : auteur2;
    }
}
