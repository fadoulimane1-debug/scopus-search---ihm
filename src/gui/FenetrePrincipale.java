package gui;

import interfaces.Affichable;
import models.Article;
import models.Notification;
import models.RequeteRecherche;
import services.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre principale de l'application Scopus Search.
 * Implémente Affichable. Point d'entrée du programme.
 */
public class FenetrePrincipale extends JFrame implements Affichable {

    // === SERVICES ===
    private ServiceRecherche    serviceRecherche;
    private ServiceTri          serviceTri;
    private ServiceFiltre       serviceFiltre;
    private ServiceExport       serviceExport;
    private ServiceFavoris      serviceFavoris;
    private ServiceStatistique  serviceStats;
    private ServiceComparaison  serviceComparaison;
    private ServiceAuth         serviceAuth;
    private ServiceNotification serviceNotif;
    private ServiceHistorique   serviceHistorique;

    // === ÉTAT ===
    private List<Article> resultatsActuels = new ArrayList<>();

    // === COMPOSANTS RECHERCHE ===
    private JTextField  champRecherche, champAuteur;
    private JComboBox<String> comboAnneeMin, comboAnneeMax, comboTri;
    private JCheckBox   checkOpenAccess;
    private JSpinner    spinnerCitations;
    private JTable      tableResultats;
    private DefaultTableModel tableModel;
    private JLabel      labelResultats;
    private JTextArea   areaDetail;

    // === COMPOSANTS BARRE HAUT ===
    private JLabel labelUtilisateur;
    private JButton btnNotifications;
    private JLabel  labelFavoris;

    // === PANNEAUX SECONDAIRES ===
    private PanneauStatistiques panneauStats;
    private PanneauComparaison  panneauComparaison;
    private JTextArea           areaHistorique;
    private JList<String>       listNotifications;
    private DefaultListModel<String> modelNotifications;

    // === COULEURS ===
    private static final Color BLEU_SCOPUS = new Color(0, 84, 166);
    private static final Color BLEU_CLAIR  = new Color(230, 242, 255);
    private static final Color ORANGE      = new Color(255, 140, 0);
    private static final Color GRIS_FOND   = new Color(245, 247, 250);
    private static final Color BLANC       = Color.WHITE;

    public FenetrePrincipale() {
        // Initialiser tous les services
        serviceRecherche   = new ServiceRecherche();
        serviceTri         = new ServiceTri();
        serviceFiltre      = new ServiceFiltre();
        serviceExport      = new ServiceExport();
        serviceFavoris     = new ServiceFavoris();
        serviceStats       = new ServiceStatistique();
        serviceComparaison = new ServiceComparaison();
        serviceAuth        = new ServiceAuth();
        serviceNotif       = new ServiceNotification();
        serviceHistorique  = new ServiceHistorique();

        // Écran de connexion
        FenetreLogin login = new FenetreLogin(serviceAuth, serviceNotif);
        login.setVisible(true);
        if (!login.isConnecte()) System.exit(0);

        initialiserFenetre();
        serviceNotif.envoyerNotification("Application démarrée",
            "Bienvenue sur Scopus Search !", "INFO");
        mettreAJour();
    }

    private void initialiserFenetre() {
        setTitle("🔬 Scopus Search — " + serviceAuth.getUtilisateurActuel().getNom());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 860);
        setLocationRelativeTo(null);
        getContentPane().setBackground(GRIS_FOND);
        setLayout(new BorderLayout(0, 0));

        add(creerBarreHaut(), BorderLayout.NORTH);

        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("Arial", Font.BOLD, 13));

        // Onglet 1 : Recherche
        onglets.addTab("  🔍 Recherche  ",   creerOngletRecherche());

        // Onglet 2 : Statistiques
        panneauStats = new PanneauStatistiques(serviceStats);
        onglets.addTab("  📊 Statistiques  ", panneauStats);

        // Onglet 3 : Comparaison
        panneauComparaison = new PanneauComparaison(serviceRecherche, serviceComparaison);
        onglets.addTab("  ⚔️ Comparaison  ",  panneauComparaison);

        // Onglet 4 : Historique
        onglets.addTab("  📋 Historique  ",   creerOngletHistorique());

        // Onglet 5 : Notifications
        onglets.addTab("  🔔 Notifications  ", creerOngletNotifications());

        // Onglet 6 : Profil
        onglets.addTab("  👤 Profil  ",        creerOngletProfil());

        add(onglets, BorderLayout.CENTER);
        add(creerBarreBas(), BorderLayout.SOUTH);
    }

    // =========================================================
    // BARRE HAUTE
    // =========================================================
    private JPanel creerBarreHaut() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLEU_SCOPUS);
        p.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titre = new JLabel("  🔬 Scopus Search");
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(BLANC);
        p.add(titre, BorderLayout.WEST);

        // Barre de recherche rapide
        JPanel barreRapide = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        barreRapide.setBackground(BLEU_SCOPUS);
        champRecherche = new JTextField(28);
        champRecherche.setFont(new Font("Arial", Font.PLAIN, 14));
        champRecherche.setPreferredSize(new Dimension(320, 34));
        champRecherche.addActionListener(e -> lancerRecherche());

        JButton btnSearch = creerBouton("🔍", ORANGE, BLANC);
        btnSearch.setPreferredSize(new Dimension(40, 34));
        btnSearch.addActionListener(e -> lancerRecherche());

        barreRapide.add(champRecherche);
        barreRapide.add(btnSearch);
        p.add(barreRapide, BorderLayout.CENTER);

        // Droite : user + notifs + favoris
        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        droite.setBackground(BLEU_SCOPUS);

        labelFavoris = new JLabel("⭐ 0");
        labelFavoris.setFont(new Font("Arial", Font.BOLD, 13));
        labelFavoris.setForeground(new Color(255, 220, 100));

        btnNotifications = creerBouton("🔔 0", new Color(60, 120, 200), BLANC);
        btnNotifications.addActionListener(e -> {
            serviceNotif.marquerToutesCommeLues();
            btnNotifications.setText("🔔 0");
            rafraichirNotifications();
        });

        labelUtilisateur = new JLabel("👤 " + serviceAuth.getUtilisateurActuel().getNom());
        labelUtilisateur.setFont(new Font("Arial", Font.BOLD, 13));
        labelUtilisateur.setForeground(BLANC);

        JButton btnDeconn = creerBouton("🚪", new Color(200, 60, 60), BLANC);
        btnDeconn.setPreferredSize(new Dimension(34, 28));
        btnDeconn.setToolTipText("Déconnexion");
        btnDeconn.addActionListener(e -> {
            serviceAuth.deconnecter();
            dispose();
            new FenetrePrincipale().setVisible(true);
        });

        droite.add(labelFavoris);
        droite.add(btnNotifications);
        droite.add(labelUtilisateur);
        droite.add(btnDeconn);
        p.add(droite, BorderLayout.EAST);
        return p;
    }

    // =========================================================
    // ONGLET RECHERCHE
    // =========================================================
    private JSplitPane creerOngletRecherche() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            creerPanneauFiltres(), creerPanneauResultats());
        split.setDividerLocation(240);
        split.setDividerSize(4);
        split.setBorder(null);
        return split;
    }

    private JPanel creerPanneauFiltres() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANC);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 0, 1, new Color(220, 220, 220)),
            new EmptyBorder(15, 12, 15, 12)));

        JLabel t = new JLabel("🎛 Filtres avancés");
        t.setFont(new Font("Arial", Font.BOLD, 14));
        t.setForeground(BLEU_SCOPUS);
        t.setAlignmentX(LEFT_ALIGNMENT);
        p.add(t); p.add(Box.createVerticalStrut(12));

        champAuteur = new JTextField();
        champAuteur.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        String[] annees = {"0","2018","2019","2020","2021","2022","2023","2024"};
        comboAnneeMin = new JComboBox<>(annees);
        comboAnneeMin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboAnneeMax = new JComboBox<>(annees);
        comboAnneeMax.setSelectedIndex(comboAnneeMax.getItemCount() - 1);
        comboAnneeMax.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        spinnerCitations = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 100));
        spinnerCitations.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        checkOpenAccess = new JCheckBox("🔓 Open Access uniquement");
        checkOpenAccess.setBackground(BLANC);

        comboTri = new JComboBox<>(new String[]{"Citations ↓","Date ↓","Titre A→Z","Auteur A→Z"});
        comboTri.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        ajouterChampFiltre(p, "👤 Auteur", champAuteur);
        ajouterChampFiltre(p, "📅 Année min", comboAnneeMin);
        ajouterChampFiltre(p, "📅 Année max", comboAnneeMax);
        ajouterChampFiltre(p, "📊 Citations min", spinnerCitations);
        p.add(checkOpenAccess); p.add(Box.createVerticalStrut(8));
        ajouterChampFiltre(p, "↕ Trier par", comboTri);
        p.add(Box.createVerticalStrut(15));

        JButton btnAppliquer = creerBouton("✅ Appliquer", BLEU_SCOPUS, BLANC);
        btnAppliquer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnAppliquer.setAlignmentX(LEFT_ALIGNMENT);
        btnAppliquer.addActionListener(e -> lancerRecherche());
        p.add(btnAppliquer); p.add(Box.createVerticalStrut(6));

        JButton btnReset = creerBouton("🔄 Réinitialiser", new Color(150,150,150), BLANC);
        btnReset.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnReset.setAlignmentX(LEFT_ALIGNMENT);
        btnReset.addActionListener(e -> reinitialiser());
        p.add(btnReset);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private void ajouterChampFiltre(JPanel p, String label, JComponent champ) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 80, 120));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl); p.add(champ); p.add(Box.createVerticalStrut(8));
    }

    private JPanel creerPanneauResultats() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(10, 10, 5, 10));

        // Barre d'actions
        JPanel barreActions = new JPanel(new BorderLayout());
        barreActions.setBackground(GRIS_FOND);
        labelResultats = new JLabel("📄 0 articles");
        labelResultats.setFont(new Font("Arial", Font.BOLD, 13));
        labelResultats.setForeground(BLEU_SCOPUS);
        barreActions.add(labelResultats, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setBackground(GRIS_FOND);

        JButton btnFav     = creerBouton("⭐ Favori",    new Color(255,165,0), BLANC);
        JButton btnStats   = creerBouton("📊 Stats",     new Color(80,130,210), BLANC);
        JButton btnCSV     = creerBouton("📊 CSV",       new Color(34,139,34), BLANC);
        JButton btnTXT     = creerBouton("📝 TXT",       new Color(100,100,200), BLANC);

        btnFav.addActionListener(e -> ajouterFavori());
        btnStats.addActionListener(e -> afficherStatsSelection());
        btnCSV.addActionListener(e -> exporterCSV());
        btnTXT.addActionListener(e -> exporterTXT());

        btns.add(btnFav); btns.add(btnStats); btns.add(btnCSV); btns.add(btnTXT);
        barreActions.add(btns, BorderLayout.EAST);
        p.add(barreActions, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"#","Titre","Auteur","Année","Domaine","Citations","Journal","OA"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableResultats = new JTable(tableModel);
        tableResultats.setRowHeight(28);
        tableResultats.setFont(new Font("Arial", Font.PLAIN, 12));
        tableResultats.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableResultats.getTableHeader().setBackground(BLEU_SCOPUS);
        tableResultats.getTableHeader().setForeground(BLANC);
        tableResultats.setSelectionBackground(BLEU_CLAIR);
        tableResultats.setGridColor(new Color(230,230,230));
        int[] larg = {35,340,110,55,160,75,160,35};
        for (int i = 0; i < larg.length; i++)
            tableResultats.getColumnModel().getColumn(i).setPreferredWidth(larg[i]);
        tableResultats.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? BLANC : new Color(248,251,255));
                return c;
            }
        });
        tableResultats.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) afficherDetail();
        });

        // Zone détail
        areaDetail = new JTextArea(6, 40);
        areaDetail.setEditable(false);
        areaDetail.setFont(new Font("Arial", Font.PLAIN, 12));
        areaDetail.setBackground(new Color(250,252,255));
        areaDetail.setBorder(new EmptyBorder(8,10,8,10));
        areaDetail.setLineWrap(true); areaDetail.setWrapStyleWord(true);
        areaDetail.setText("👆 Cliquez sur un article pour voir ses détails.");

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(tableResultats), new JScrollPane(areaDetail));
        split.setDividerLocation(420); split.setDividerSize(4); split.setBorder(null);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    // =========================================================
    // ONGLET HISTORIQUE
    // =========================================================
    private JPanel creerOngletHistorique() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        areaHistorique = new JTextArea();
        areaHistorique.setEditable(false);
        areaHistorique.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaHistorique.setBackground(BLANC);
        areaHistorique.setBorder(new EmptyBorder(10,12,10,12));

        JScrollPane scroll = new JScrollPane(areaHistorique);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(210,220,235)),
            "📋 Historique des recherches", 0, 0,
            new Font("Arial", Font.BOLD, 13), BLEU_SCOPUS));
        p.add(scroll, BorderLayout.CENTER);

        JButton btnEffacer = creerBouton("🗑 Effacer l'historique", new Color(200,60,60), BLANC);
        btnEffacer.addActionListener(e -> {
            serviceHistorique.effacerHistorique();
            areaHistorique.setText("");
        });
        JPanel bas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bas.setBackground(GRIS_FOND); bas.add(btnEffacer);
        p.add(bas, BorderLayout.SOUTH);
        return p;
    }

    // =========================================================
    // ONGLET NOTIFICATIONS
    // =========================================================
    private JPanel creerOngletNotifications() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        modelNotifications = new DefaultListModel<>();
        listNotifications  = new JList<>(modelNotifications);
        listNotifications.setFont(new Font("Arial", Font.PLAIN, 13));
        listNotifications.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, sel, focus);
                l.setBorder(new EmptyBorder(8, 12, 8, 12));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(listNotifications);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(210,220,235)),
            "🔔 Toutes les notifications", 0, 0,
            new Font("Arial", Font.BOLD, 13), BLEU_SCOPUS));
        p.add(scroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(GRIS_FOND);
        JButton btnLu  = creerBouton("✅ Tout marquer lu",  BLEU_SCOPUS, BLANC);
        JButton btnDel = creerBouton("🗑 Tout supprimer",   new Color(200,60,60), BLANC);
        btnLu.addActionListener(e -> {
            serviceNotif.marquerToutesCommeLues();
            btnNotifications.setText("🔔 0");
            rafraichirNotifications();
        });
        btnDel.addActionListener(e -> {
            serviceNotif.supprimerToutesNotifications();
            btnNotifications.setText("🔔 0");
            rafraichirNotifications();
        });
        btns.add(btnLu); btns.add(btnDel);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    // =========================================================
    // ONGLET PROFIL
    // =========================================================
    private JPanel creerOngletProfil() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(GRIS_FOND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel carte = new JPanel(new GridBagLayout());
        carte.setBackground(BLANC);
        carte.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210,220,235)),
            new EmptyBorder(25, 30, 25, 30)));

        JLabel titreP = new JLabel("👤 Mon Profil", SwingConstants.CENTER);
        titreP.setFont(new Font("Arial", Font.BOLD, 20));
        titreP.setForeground(BLEU_SCOPUS);

        JTextField fNom  = new JTextField(serviceAuth.getUtilisateurActuel().getNom(), 20);
        JTextField fEmail = new JTextField(serviceAuth.getUtilisateurActuel().getEmail(), 20);
        fEmail.setEditable(false); fEmail.setBackground(new Color(240,240,240));
        JTextField fInst = new JTextField(serviceAuth.getUtilisateurActuel().getInstitution(), 20);
        JTextField fDom  = new JTextField(serviceAuth.getUtilisateurActuel().getDomaine(), 20);

        for (JTextField f : new JTextField[]{fNom, fEmail, fInst, fDom}) {
            f.setFont(new Font("Arial", Font.PLAIN, 13));
            f.setPreferredSize(new Dimension(250, 32));
        }

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(8, 10, 8, 10); g2.fill = GridBagConstraints.HORIZONTAL;
        ajouterLigneForm(carte, g2, 0, titreP, null);
        ajouterLigneForm(carte, g2, 1, new JLabel("Nom complet :"), fNom);
        ajouterLigneForm(carte, g2, 2, new JLabel("Email :"), fEmail);
        ajouterLigneForm(carte, g2, 3, new JLabel("Institution :"), fInst);
        ajouterLigneForm(carte, g2, 4, new JLabel("Domaine de recherche :"), fDom);

        JButton btnSauv = creerBouton("💾 Sauvegarder le profil", BLEU_SCOPUS, BLANC);
        btnSauv.addActionListener(e -> {
            serviceAuth.getUtilisateurActuel().setNom(fNom.getText().trim());
            serviceAuth.getUtilisateurActuel().setInstitution(fInst.getText().trim());
            serviceAuth.getUtilisateurActuel().setDomaine(fDom.getText().trim());
            labelUtilisateur.setText("👤 " + serviceAuth.getUtilisateurActuel().getNom());
            serviceNotif.envoyerNotification("Profil mis à jour", "Vos informations ont été sauvegardées.", "SUCCES");
            rafraichirNotifications();
            JOptionPane.showMessageDialog(this, "✅ Profil mis à jour !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        });
        ajouterLigneForm(carte, g2, 5, btnSauv, null);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(carte, gbc);
        return p;
    }

    private void ajouterLigneForm(JPanel p, GridBagConstraints gbc, int row, Component c1, Component c2) {
        gbc.gridy = row;
        if (c2 == null) {
            gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            p.add(c1, gbc); gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        } else {
            if (c1 instanceof JLabel) ((JLabel)c1).setFont(new Font("Arial", Font.BOLD, 12));
            gbc.gridx = 0; gbc.weightx = 0.3; p.add(c1, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7; p.add(c2, gbc);
        }
    }

    // =========================================================
    // BARRE BAS
    // =========================================================
    private JPanel creerBarreBas() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        p.setBackground(new Color(235,240,250));
        p.setBorder(new MatteBorder(1,0,0,0, new Color(210,220,235)));
        JLabel lbl = new JLabel("© Scopus Search — Mini Projet IHM Java POO | 10 Interfaces");
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(new Color(100,100,120));
        p.add(lbl);
        return p;
    }

    // =========================================================
    // LOGIQUE
    // =========================================================
    private void lancerRecherche() {
        String motCle = champRecherche.getText().trim();
        String auteur = champAuteur != null ? champAuteur.getText().trim() : "";
        int anneeMin  = Integer.parseInt((String) comboAnneeMin.getSelectedItem());
        int anneeMax  = Integer.parseInt((String) comboAnneeMax.getSelectedItem());
        int citMin    = (int) spinnerCitations.getValue();

        List<Article> res = serviceRecherche.rechercheAvancee(motCle, auteur, anneeMin, anneeMax, "");

        if (citMin > 0)              res = serviceFiltre.filtrerParCitations(res, citMin);
        if (checkOpenAccess.isSelected()) res = serviceFiltre.filtrerOpenAccess(res);

        switch (comboTri.getSelectedIndex()) {
            case 0: res = serviceTri.trierParCitations(res); break;
            case 1: res = serviceTri.trierParDate(res); break;
            case 2: res = serviceTri.trierParTitre(res); break;
            case 3: res = serviceTri.trierParAuteur(res); break;
        }

        resultatsActuels = res;
        serviceHistorique.ajouterRequete(new RequeteRecherche(motCle, auteur, res.size()));
        rafraichirHistorique();

        serviceNotif.envoyerNotification("Recherche effectuée",
            res.size() + " article(s) trouvé(s) pour \"" + (motCle.isEmpty() ? auteur : motCle) + "\"",
            res.isEmpty() ? "ALERTE" : "SUCCES");
        mettreAJour();
    }

    private void afficherStatsSelection() {
        if (resultatsActuels.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun résultat à analyser.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        panneauStats.setArticles(resultatsActuels);
        // Switcher vers l'onglet Stats
        JTabbedPane tabs = (JTabbedPane) ((JPanel) getContentPane().getComponent(1)).getParent();
        Container c = getContentPane();
        for (Component comp : c.getComponents()) {
            if (comp instanceof JTabbedPane) {
                ((JTabbedPane) comp).setSelectedIndex(1);
                break;
            }
        }
    }

    private void ajouterFavori() {
        int row = tableResultats.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un article.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        Article a = resultatsActuels.get(row);
        if (serviceFavoris.estSauvegarde(a)) {
            JOptionPane.showMessageDialog(this, "Déjà dans vos favoris !", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            serviceFavoris.sauvegarder(a);
            serviceNotif.envoyerNotification("Favori ajouté", "\"" + a.getTitre().substring(0, Math.min(40, a.getTitre().length())) + "...\" ajouté.", "SUCCES");
            mettreAJour();
        }
    }

    private void exporterCSV() {
        if (resultatsActuels.isEmpty()) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("scopus_results.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            serviceExport.exporterCSV(resultatsActuels, fc.getSelectedFile().getAbsolutePath());
            serviceNotif.envoyerNotification("Export CSV", "Fichier exporté avec succès.", "SUCCES");
            JOptionPane.showMessageDialog(this, "✅ Export CSV réussi !", "Export", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exporterTXT() {
        if (resultatsActuels.isEmpty()) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("scopus_results.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            serviceExport.exporterTXT(resultatsActuels, fc.getSelectedFile().getAbsolutePath());
            serviceNotif.envoyerNotification("Export TXT", "Fichier exporté avec succès.", "SUCCES");
            JOptionPane.showMessageDialog(this, "✅ Export TXT réussi !", "Export", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void afficherDetail() {
        int row = tableResultats.getSelectedRow();
        if (row < 0 || row >= resultatsActuels.size()) return;
        Article a = resultatsActuels.get(row);
        areaDetail.setText(
            "📌 Titre    : " + a.getTitre() + "\n" +
            "👤 Auteur   : " + a.getAuteur() + "\n" +
            "📅 Année    : " + a.getAnnee() + "    |    📊 Citations : " + a.getCitations() + "\n" +
            "📰 Journal  : " + a.getJournal() + "\n" +
            "🏷  Domaine  : " + a.getDomaine() + "    |    " + (a.isOpenAccess() ? "🔓 Open Access" : "🔒 Accès restreint") + "\n" +
            "🔗 DOI      : " + a.getDoi() + "\n\n" +
            "📝 Résumé   : " + a.getResume());
    }

    private void reinitialiser() {
        champRecherche.setText(""); champAuteur.setText("");
        comboAnneeMin.setSelectedIndex(0);
        comboAnneeMax.setSelectedIndex(comboAnneeMax.getItemCount()-1);
        spinnerCitations.setValue(0);
        checkOpenAccess.setSelected(false);
        comboTri.setSelectedIndex(0);
        resultatsActuels = new ArrayList<>();
        mettreAJour();
    }

    private void rafraichirNotifications() {
        if (modelNotifications == null) return;
        modelNotifications.clear();
        for (Notification n : serviceNotif.getToutesNotifications()) {
            modelNotifications.addElement(n.getIcone() + " [" + n.getDateHeure() + "] " +
                n.getTitre() + " — " + n.getMessage() + (n.isLue() ? "" : " 🔵"));
        }
        int nonLues = serviceNotif.getNombreNonLues();
        btnNotifications.setText("🔔 " + nonLues);
    }

    private void rafraichirHistorique() {
        if (areaHistorique == null) return;
        StringBuilder sb = new StringBuilder();
        for (RequeteRecherche r : serviceHistorique.getHistorique()) {
            sb.append(r.toString()).append("\n");
        }
        areaHistorique.setText(sb.toString());
    }

    private void remplirTableau(List<Article> articles) {
        tableModel.setRowCount(0);
        for (int i = 0; i < articles.size(); i++) {
            Article a = articles.get(i);
            tableModel.addRow(new Object[]{i+1, a.getTitre(), a.getAuteur(), a.getAnnee(),
                a.getDomaine(), a.getCitations(), a.getJournal(), a.isOpenAccess() ? "🔓" : "🔒"});
        }
    }

    // =========================================================
    // INTERFACE Affichable
    // =========================================================
    @Override
    public void afficher() { mettreAJour(); }

    @Override
    public void mettreAJour() {
        remplirTableau(resultatsActuels);
        labelResultats.setText("📄 " + resultatsActuels.size() + " article(s)");
        labelFavoris.setText("⭐ " + serviceFavoris.getNombreFavoris());
        rafraichirNotifications();
        rafraichirHistorique();
    }

    @Override
    public void effacer() { resultatsActuels.clear(); mettreAJour(); }

    // =========================================================
    // UTILITAIRES
    // =========================================================
    private JButton creerBouton(String texte, Color fond, Color fg) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(fond); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6,12,6,12));
        return btn;
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new FenetrePrincipale().setVisible(true);
        });
    }
}
