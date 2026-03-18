package interfaces;

import models.Utilisateur;

/**
 * Interface pour la gestion de l'authentification utilisateur.
 * NOUVELLE INTERFACE pour le système de login/profil.
 */
public interface Authentifiable {

    /**
     * Connecte un utilisateur avec email et mot de passe
     * @return true si la connexion est réussie
     */
    boolean connecter(String email, String motDePasse);

    /**
     * Déconnecte l'utilisateur actuel
     */
    void deconnecter();

    /**
     * Enregistre un nouvel utilisateur
     * @return true si l'inscription est réussie
     */
    boolean inscrire(String nom, String email, String motDePasse);

    /**
     * Retourne l'utilisateur actuellement connecté
     */
    Utilisateur getUtilisateurActuel();

    /**
     * Vérifie si un utilisateur est connecté
     */
    boolean estConnecte();

    /**
     * Met à jour le profil de l'utilisateur
     */
    boolean mettreAJourProfil(Utilisateur utilisateur);
}
