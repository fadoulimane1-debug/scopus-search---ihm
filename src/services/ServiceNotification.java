package services;

import interfaces.Notifiable;
import models.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des notifications.
 * Implémente l'interface Notifiable.
 */
public class ServiceNotification implements Notifiable {

    private List<Notification> notifications = new ArrayList<>();

    @Override
    public void envoyerNotification(String titre, String message, String type) {
        Notification.Type t;
        switch (type.toUpperCase()) {
            case "SUCCES":  t = Notification.Type.SUCCES;  break;
            case "ALERTE":  t = Notification.Type.ALERTE;  break;
            case "ERREUR":  t = Notification.Type.ERREUR;  break;
            default:        t = Notification.Type.INFO;    break;
        }
        notifications.add(0, new Notification(titre, message, t)); // plus récent en premier
    }

    @Override
    public List<Notification> getNotificationsNonLues() {
        return notifications.stream()
            .filter(n -> !n.isLue())
            .collect(Collectors.toList());
    }

    @Override
    public void marquerCommeLue(Notification notification) {
        notification.setLue(true);
    }

    @Override
    public void marquerToutesCommeLues() {
        notifications.forEach(n -> n.setLue(true));
    }

    @Override
    public void supprimerToutesNotifications() {
        notifications.clear();
    }

    @Override
    public int getNombreNonLues() {
        return (int) notifications.stream().filter(n -> !n.isLue()).count();
    }

    public List<Notification> getToutesNotifications() {
        return new ArrayList<>(notifications);
    }
}
