package services;

import interfaces.Historique;
import models.RequeteRecherche;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de gestion de l'historique de recherches.
 * Implémente l'interface Historique.
 */
public class ServiceHistorique implements Historique {

    private List<RequeteRecherche> historique = new ArrayList<>();

    @Override
    public void ajouterRequete(RequeteRecherche requete) {
        historique.add(0, requete);
        if (historique.size() > 50) historique.remove(historique.size() - 1);
    }

    @Override
    public List<RequeteRecherche> getHistorique() {
        return new ArrayList<>(historique);
    }

    @Override
    public void effacerHistorique() { historique.clear(); }

    @Override
    public List<RequeteRecherche> getDernieresRecherches(int n) {
        return historique.stream().limit(n).collect(Collectors.toList());
    }

    @Override
    public List<String> getRecherchesFrequentes() {
        Map<String, Long> freq = new HashMap<>();
        for (RequeteRecherche r : historique) {
            String cle = !r.getMotCle().isEmpty() ? r.getMotCle() : r.getAuteur();
            if (!cle.isEmpty()) freq.merge(cle, 1L, Long::sum);
        }
        return freq.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}
