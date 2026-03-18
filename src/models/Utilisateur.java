package models;

/**
 * Classe représentant un utilisateur de l'application.
 */
public class Utilisateur {
    private String nom;
    private String email;
    private String motDePasse;
    private String institution;
    private String domaine;

    public Utilisateur(String nom, String email, String motDePasse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.institution = "";
        this.domaine = "";
    }

    public String getNom()          { return nom; }
    public String getEmail()        { return email; }
    public String getMotDePasse()   { return motDePasse; }
    public String getInstitution()  { return institution; }
    public String getDomaine()      { return domaine; }

    public void setNom(String nom)              { this.nom = nom; }
    public void setEmail(String email)          { this.email = email; }
    public void setMotDePasse(String mdp)       { this.motDePasse = mdp; }
    public void setInstitution(String inst)     { this.institution = inst; }
    public void setDomaine(String domaine)      { this.domaine = domaine; }

    @Override
    public String toString() { return nom + " <" + email + ">"; }
}
