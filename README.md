# 🔬 Scopus Search v2 — Mini Projet IHM Java POO

## Structure complète
```
scopus2/src/
├── interfaces/          ← 10 INTERFACES
│   ├── Recherchable.java
│   ├── Affichable.java
│   ├── Exportable.java
│   ├── Triable.java
│   ├── Filtrable.java
│   ├── Sauvegardable.java
│   ├── Statistique.java     ← NOUVELLE
│   ├── Comparable.java      ← NOUVELLE
│   ├── Authentifiable.java  ← NOUVELLE
│   ├── Notifiable.java      ← NOUVELLE
│   └── Historique.java      ← NOUVELLE
├── models/
│   ├── Article.java
│   ├── Utilisateur.java
│   ├── Notification.java
│   ├── ResultatComparaison.java
│   └── RequeteRecherche.java
├── services/            ← Chaque service implémente une interface
│   ├── ServiceRecherche.java    → Recherchable
│   ├── ServiceTri.java          → Triable
│   ├── ServiceFiltre.java       → Filtrable
│   ├── ServiceExport.java       → Exportable
│   ├── ServiceFavoris.java      → Sauvegardable
│   ├── ServiceStatistique.java  → Statistique
│   ├── ServiceComparaison.java  → Comparable
│   ├── ServiceAuth.java         → Authentifiable
│   ├── ServiceNotification.java → Notifiable
│   └── ServiceHistorique.java   → Historique
└── gui/
    ├── FenetreLogin.java         → Connexion/Inscription
    ├── FenetrePrincipale.java    → Affichable + main()
    ├── PanneauStatistiques.java  → Affichable + graphiques
    └── PanneauComparaison.java   → Affichable

## 10 Interfaces utilisées
| Interface       | Implémentée par           |
|-----------------|---------------------------|
| Recherchable    | ServiceRecherche          |
| Affichable      | FenetrePrincipale, PanneauStatistiques, PanneauComparaison |
| Exportable      | ServiceExport             |
| Triable         | ServiceTri                |
| Filtrable       | ServiceFiltre             |
| Sauvegardable   | ServiceFavoris            |
| Statistique     | ServiceStatistique        |
| Comparable      | ServiceComparaison        |
| Authentifiable  | ServiceAuth               |
| Notifiable      | ServiceNotification       |
| Historique      | ServiceHistorique         |

## Fonctionnalités
- 🔐 Login / Inscription avec validation
- 🔍 Recherche via vraie API Scopus
- 🎛 Filtres avancés + tri
- 📊 Statistiques avec graphiques (barres + courbe)
- ⚔️ Comparaison entre 2 auteurs (h-index, citations...)
- ⭐ Favoris
- 🔔 Système de notifications
- 📋 Historique des recherches
- 👤 Gestion du profil utilisateur
- 📤 Export CSV / TXT

