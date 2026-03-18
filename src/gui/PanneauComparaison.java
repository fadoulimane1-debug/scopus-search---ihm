package gui;

import interfaces.Affichable;
import models.Article;
import models.ResultatComparaison;
import services.ServiceComparaison;
import services.ServiceRecherche;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panneau de comparaison entre deux auteurs scientifiques.
 * Implémente Affichable.
 */
public class PanneauComparaison extends JPanel implements Affichable {

    private ServiceRecherche   serviceRecherche;
    private ServiceComparaison serviceComparaison;

    private JTextField champAuteur1, champAuteur2;
    private JLabel     lblNom1, lblNom2;
    private JLabel     lblPub1, lblPub2, lblPubGagnant;
    private JLabel     lblCit1, lblCit2, lblCitGagnant;
    private JLabel     lblH1,   lblH2,   lblHGagnant;
    private JLabel     lblMoy1, lblMoy2, lblMoyGagnant;
    private JLabel     lblGagnant;
    private JProgressBar barPub1, barPub2, barCit1, barCit2;

    private static final Color BLEU   = new Color(0, 84, 166);
    private static final Color VERT   = new Color(34, 139, 34);
    private static final Color ORANGE = new Color(200, 100, 0);
    private static final Color BLANC  = Color.WHITE;
    private static final Color FOND   = new Color(245, 247, 250);

    public PanneauComparaison(ServiceRecherche sr, ServiceComparaison sc) {
        this.serviceRecherche   = sr;
        this.serviceComparaison = sc;
        setBackground(FOND);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        construireUI();
    }

    private void construireUI() {
        // --- Barre de recherche ---
        JPanel barreRecherche = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        barreRecherche.setBackground(BLANC);
        barreRecherche.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 245)),
            new EmptyBorder(8, 10, 8, 10)));

        champAuteur1 = new JTextField(15); champAuteur1.setFont(new Font("Arial", Font.PLAIN, 13));
        champAuteur2 = new JTextField(15); champAuteur2.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel vs = new JLabel("⚔️  VS  ⚔️");
        vs.setFont(new Font("Arial", Font.BOLD, 16)); vs.setForeground(ORANGE);

        JButton btnComparer = new JButton("🔍 Comparer");
        btnComparer.setFont(new Font("Arial", Font.BOLD, 13));
        btnComparer.setBackground(BLEU); btnComparer.setForeground(BLANC);
        btnComparer.setFocusPainted(false); btnComparer.setBorderPainted(false);
        btnComparer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnComparer.addActionListener(e -> lancerComparaison());

        barreRecherche.add(new JLabel("👤 Auteur 1:"));
        barreRecherche.add(champAuteur1);
        barreRecherche.add(vs);
        barreRecherche.add(new JLabel("👤 Auteur 2:"));
        barreRecherche.add(champAuteur2);
        barreRecherche.add(btnComparer);
        add(barreRecherche, BorderLayout.NORTH);

        // --- Tableau de comparaison ---
        JPanel tableauComp = new JPanel(new GridBagLayout());
        tableauComp.setBackground(BLANC);
        tableauComp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 235)),
            new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // En-têtes
        lblNom1 = creerLabelNom("Auteur 1", BLEU);
        lblNom2 = creerLabelNom("Auteur 2", VERT);
        ajouterLigne(tableauComp, gbc, 0, creerLabelCritere(""), lblNom1, creerLabelCritere(""), lblNom2, creerLabelCritere("🏆"));

        // Lignes de données
        lblPub1 = creerLabelVal("—"); lblPub2 = creerLabelVal("—"); lblPubGagnant = creerLabelGagnant("—");
        lblCit1 = creerLabelVal("—"); lblCit2 = creerLabelVal("—"); lblCitGagnant = creerLabelGagnant("—");
        lblH1   = creerLabelVal("—"); lblH2   = creerLabelVal("—"); lblHGagnant   = creerLabelGagnant("—");
        lblMoy1 = creerLabelVal("—"); lblMoy2 = creerLabelVal("—"); lblMoyGagnant = creerLabelGagnant("—");

        barPub1 = creerBarre(BLEU); barPub2 = creerBarre(VERT);
        barCit1 = creerBarre(BLEU); barCit2 = creerBarre(VERT);

        ajouterLigne(tableauComp, gbc, 1, creerLabelCritere("📄 Publications"), lblPub1, creerLabelCritere(""), lblPub2, lblPubGagnant);
        ajouterLigne(tableauComp, gbc, 2, creerLabelCritere(""), barPub1, creerLabelCritere(""), barPub2, creerLabelCritere(""));
        ajouterLigne(tableauComp, gbc, 3, creerLabelCritere("📊 Citations totales"), lblCit1, creerLabelCritere(""), lblCit2, lblCitGagnant);
        ajouterLigne(tableauComp, gbc, 4, creerLabelCritere(""), barCit1, creerLabelCritere(""), barCit2, creerLabelCritere(""));
        ajouterLigne(tableauComp, gbc, 5, creerLabelCritere("📈 Moyenne citations"), lblMoy1, creerLabelCritere(""), lblMoy2, lblMoyGagnant);
        ajouterLigne(tableauComp, gbc, 6, creerLabelCritere("🏅 H-Index"), lblH1, creerLabelCritere(""), lblH2, lblHGagnant);

        // Séparateur
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 5;
        JSeparator sep = new JSeparator(); sep.setForeground(new Color(220, 230, 245));
        tableauComp.add(sep, gbc);

        // Gagnant global
        lblGagnant = new JLabel("🏆 Lancez une comparaison !", SwingConstants.CENTER);
        lblGagnant.setFont(new Font("Arial", Font.BOLD, 18));
        lblGagnant.setForeground(ORANGE);
        gbc.gridy = 8; gbc.gridwidth = 5;
        gbc.insets = new Insets(15, 0, 5, 0);
        tableauComp.add(lblGagnant, gbc);

        add(tableauComp, BorderLayout.CENTER);

        // Conseil
        JLabel conseil = new JLabel("  💡 Exemple : tapez \"Hinton\" et \"LeCun\" puis cliquez Comparer");
        conseil.setFont(new Font("Arial", Font.ITALIC, 12));
        conseil.setForeground(new Color(120, 120, 140));
        add(conseil, BorderLayout.SOUTH);
    }

    private void lancerComparaison() {
        String a1 = champAuteur1.getText().trim();
        String a2 = champAuteur2.getText().trim();
        if (a1.isEmpty() || a2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Entrez les deux noms d'auteurs !", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        lblGagnant.setText("⏳ Recherche en cours...");
        lblNom1.setText(a1); lblNom2.setText(a2);

        SwingWorker<ResultatComparaison, Void> worker = new SwingWorker<>() {
            @Override
            protected ResultatComparaison doInBackground() {
                List<Article> arts1 = serviceRecherche.rechercherParAuteur(a1);
                List<Article> arts2 = serviceRecherche.rechercherParAuteur(a2);
                return serviceComparaison.comparerAuteurs(a1, a2, arts1, arts2);
            }
            @Override
            protected void done() {
                try {
                    ResultatComparaison res = get();
                    afficherResultat(res);
                } catch (Exception e) {
                    lblGagnant.setText("❌ Erreur lors de la comparaison");
                }
            }
        };
        worker.execute();
    }

    private void afficherResultat(ResultatComparaison res) {
        lblPub1.setText(String.valueOf(res.getPublications1()));
        lblPub2.setText(String.valueOf(res.getPublications2()));
        lblCit1.setText(String.valueOf(res.getCitations1()));
        lblCit2.setText(String.valueOf(res.getCitations2()));
        lblH1.setText(String.valueOf(res.getHIndex1()));
        lblH2.setText(String.valueOf(res.getHIndex2()));
        lblMoy1.setText(String.format("%.1f", res.getMoyenneCitations1()));
        lblMoy2.setText(String.format("%.1f", res.getMoyenneCitations2()));

        int maxPub = Math.max(res.getPublications1(), res.getPublications2());
        int maxCit = Math.max(res.getCitations1(), res.getCitations2());
        if (maxPub > 0) {
            barPub1.setValue(res.getPublications1() * 100 / maxPub);
            barPub2.setValue(res.getPublications2() * 100 / maxPub);
        }
        if (maxCit > 0) {
            barCit1.setValue(res.getCitations1() * 100 / maxCit);
            barCit2.setValue(res.getCitations2() * 100 / maxCit);
        }

        lblPubGagnant.setText(res.getPublications1() >= res.getPublications2() ? "✅ " + res.getAuteur1() : "✅ " + res.getAuteur2());
        lblCitGagnant.setText(res.getCitations1()    >= res.getCitations2()    ? "✅ " + res.getAuteur1() : "✅ " + res.getAuteur2());
        lblHGagnant.setText(  res.getHIndex1()        >= res.getHIndex2()       ? "✅ " + res.getAuteur1() : "✅ " + res.getAuteur2());
        lblMoyGagnant.setText(res.getMoyenneCitations1() >= res.getMoyenneCitations2() ? "✅ " + res.getAuteur1() : "✅ " + res.getAuteur2());

        lblGagnant.setText("🏆 Meilleur profil global : " + res.getGagnant());
    }

    private void ajouterLigne(JPanel p, GridBagConstraints gbc, int row,
                               Component c1, Component c2, Component c3, Component c4, Component c5) {
        gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; p.add(c1, gbc);
        gbc.gridx = 1; gbc.weightx = 0.35; p.add(c2, gbc);
        gbc.gridx = 2; gbc.weightx = 0; p.add(c3, gbc);
        gbc.gridx = 3; gbc.weightx = 0.35; p.add(c4, gbc);
        gbc.gridx = 4; gbc.weightx = 0; p.add(c5, gbc);
    }

    private JLabel creerLabelNom(String t, Color c) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 15)); l.setForeground(c); return l;
    }
    private JLabel creerLabelCritere(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(new Color(80, 80, 100)); return l;
    }
    private JLabel creerLabelVal(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 20)); l.setForeground(new Color(40, 40, 80)); return l;
    }
    private JLabel creerLabelGagnant(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 11)); l.setForeground(VERT); return l;
    }
    private JProgressBar creerBarre(Color c) {
        JProgressBar b = new JProgressBar(0, 100); b.setValue(0);
        b.setForeground(c); b.setBackground(new Color(230, 235, 245));
        b.setPreferredSize(new Dimension(150, 12)); b.setBorderPainted(false); return b;
    }

    @Override public void afficher()    { repaint(); }
    @Override public void mettreAJour() { repaint(); }
    @Override public void effacer() {
        champAuteur1.setText(""); champAuteur2.setText("");
        lblGagnant.setText("🏆 Lancez une comparaison !");
    }
}
