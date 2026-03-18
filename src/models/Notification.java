package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe représentant une notification système.
 */
public class Notification {
    public enum Type { INFO, SUCCES, ALERTE, ERREUR }

    private String titre;
    private String message;
    private Type type;
    private boolean lue;
    private LocalDateTime dateHeure;

    public Notification(String titre, String message, Type type) {
        this.titre = titre;
        this.message = message;
        this.type = type;
        this.lue = false;
        this.dateHeure = LocalDateTime.now();
    }

    public String getTitre()       { return titre; }
    public String getMessage()     { return message; }
    public Type getType()          { return type; }
    public boolean isLue()         { return lue; }
    public void setLue(boolean lue){ this.lue = lue; }
    public String getDateHeure() {
        return dateHeure.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }

    public String getIcone() {
        switch (type) {
            case SUCCES:  return "✅";
            case ALERTE:  return "⚠️";
            case ERREUR:  return "❌";
            default:      return "ℹ️";
        }
    }
}
