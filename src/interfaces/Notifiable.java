package interfaces;

import models.Notification;
import java.util.List;

/**
 * Interface pour la gestion des notifications et alertes.
 * NOUVELLE INTERFACE pour le système de notifications.
 */
public interface Notifiable {

    /**
     * Envoie une notification à l'utilisateur
     */
    void envoyerNotification(String titre, String message, String type);

    /**
     * Retourne toutes les notifications non lues
     */
    List<Notification> getNotificationsNonLues();

    /**
     * Marque une notification comme lue
     */
    void marquerCommeLue(Notification notification);

    /**
     * Marque toutes les notifications comme lues
     */
    void marquerToutesCommeLues();

    /**
     * Supprime toutes les notifications
     */
    void supprimerToutesNotifications();

    /**
     * Retourne le nombre de notifications non lues
     */
    int getNombreNonLues();
}
