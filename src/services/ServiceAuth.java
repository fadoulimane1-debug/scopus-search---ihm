package services;

import interfaces.Authentifiable;
import models.Utilisateur;
import java.util.ArrayList;
import java.util.List;

/**
 * Service d'authentification des utilisateurs.
 * Implémente l'interface Authentifiable.
 */
public class ServiceAuth implements Authentifiable {

    private List<Utilisateur> utilisateurs = new ArrayList<>();
    private Utilisateur utilisateurActuel  = null;

    public ServiceAuth() {
        // Compte de démonstration par défaut
        utilisateurs.add(new Utilisateur("Admin", "admin@scopus.com", "1234"));
    }

    @Override
    public boolean connecter(String email, String motDePasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) &&
                u.getMotDePasse().equals(motDePasse)) {
                utilisateurActuel = u;
                return true;
            }
        }
        return false;
    }

    @Override
    public void deconnecter() {
        utilisateurActuel = null;
    }

    @Override
    public boolean inscrire(String nom, String email, String motDePasse) {
        // Vérifier si l'email existe déjà
        boolean existe = utilisateurs.stream()
            .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (existe) return false;

        Utilisateur nouvel = new Utilisateur(nom.trim(), email.trim(), motDePasse);
        utilisateurs.add(nouvel);
        utilisateurActuel = nouvel;
        return true;
    }

    @Override
    public Utilisateur getUtilisateurActuel() { return utilisateurActuel; }

    @Override
    public boolean estConnecte() { return utilisateurActuel != null; }

    @Override
    public boolean mettreAJourProfil(Utilisateur utilisateur) {
        if (utilisateurActuel == null) return false;
        utilisateurActuel.setNom(utilisateur.getNom());
        utilisateurActuel.setInstitution(utilisateur.getInstitution());
        utilisateurActuel.setDomaine(utilisateur.getDomaine());
        return true;
    }
}
