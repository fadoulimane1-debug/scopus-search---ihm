package gui;

import interfaces.Affichable;
import models.Article;
import services.ServiceStatistique;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panneau d'affichage des statistiques avec graphiques.
 * Implémente Affichable.
 */
public class PanneauStatistiques extends JPanel implements Affichable {

    private ServiceStatistique serviceStats;
    private List<Article>      articles;

    private JLabel lblTotal, lblCitations, lblMoyenne, lblOpenAccess, lblPlusCite;
    private GraphiqueDomaines graphiqueDomaines;
    private GraphiqueAnnees   graphiqueAnnees;

    private static final Color BLEU  = new Color(0, 84, 166);
    private static final Color BLANC = Color.WHITE;
    private static final Color FOND  = new Color(245, 247, 250);

    public PanneauStatistiques(ServiceStatistique serviceStats) {
        this.serviceStats = serviceStats;
        this.articles = java.util.Collections.emptyList();
        setBackground(FOND);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        construireUI();
    }

    private void construireUI() {
        // --- Cartes de stats en haut ---
        JPanel cartesPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        cartesPanel.setBackground(FOND);

        lblTotal      = creerCarte("📄 Articles",   "0",    new Color(0, 84, 166));
        lblCitations  = creerCarte("📊 Citations",  "0",    new Color(34, 139, 34));
        lblMoyenne    = creerCarte("📈 Moyenne",    "0.0",  new Color(180, 100, 0));
        lblOpenAccess = creerCarte("🔓 Open Access","0%",   new Color(100, 60, 180));
        lblPlusCite   = creerCarte("🏆 Top cité",   "—",    new Color(180, 30, 30));

        cartesPanel.add(lblTotal.getParent());
        cartesPanel.add(lblCitations.getParent());
        cartesPanel.add(lblMoyenne.getParent());
        cartesPanel.add(lblOpenAccess.getParent());
        cartesPanel.add(lblPlusCite.getParent());
        add(cartesPanel, BorderLayout.NORTH);

        // --- Graphiques ---
        graphiqueDomaines = new GraphiqueDomaines();
        graphiqueAnnees   = new GraphiqueAnnees();

        JPanel graphPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        graphPanel.setBackground(FOND);

        JPanel wrapDomaines = wrapper(graphiqueDomaines, "📊 Articles par domaine");
        JPanel wrapAnnees   = wrapper(graphiqueAnnees,   "📅 Articles par année");

        graphPanel.add(wrapDomaines);
        graphPanel.add(wrapAnnees);
        add(graphPanel, BorderLayout.CENTER);
    }

    private JPanel wrapper(JPanel inner, String titre) {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(BLANC);
        w.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 235)),
            titre, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12), BLEU));
        w.add(inner, BorderLayout.CENTER);
        return w;
    }

    private JLabel creerCarte(String titre, String valeur, Color couleur) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setBackground(BLANC);
        carte.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 245), 1),
            new EmptyBorder(12, 10, 12, 10)));

        JLabel lblTitre = new JLabel(titre, SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitre.setForeground(new Color(100, 100, 120));

        JLabel lblVal = new JLabel(valeur, SwingConstants.CENTER);
        lblVal.setFont(new Font("Arial", Font.BOLD, 22));
        lblVal.setForeground(couleur);

        carte.add(lblTitre, BorderLayout.NORTH);
        carte.add(lblVal,   BorderLayout.CENTER);
        return lblVal;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        mettreAJour();
    }

    @Override
    public void afficher() { mettreAJour(); }

    @Override
    public void mettreAJour() {
        if (articles == null || articles.isEmpty()) {
            lblTotal.setText("0");
            lblCitations.setText("0");
            lblMoyenne.setText("0.0");
            lblOpenAccess.setText("0%");
            lblPlusCite.setText("—");
            graphiqueDomaines.setDonnees(new java.util.LinkedHashMap<>());
            graphiqueAnnees.setDonnees(new java.util.TreeMap<>());
            return;
        }

        lblTotal.setText(String.valueOf(articles.size()));
        lblCitations.setText(String.valueOf(serviceStats.calculerTotalCitations(articles)));
        lblMoyenne.setText(String.format("%.1f", serviceStats.calculerMoyenneCitations(articles)));
        lblOpenAccess.setText(String.format("%.0f%%", serviceStats.getPourcentageOpenAccess(articles)));

        Article top = serviceStats.getArticlePlusCite(articles);
        lblPlusCite.setText(top != null ? top.getCitations() + " cit." : "—");

        graphiqueDomaines.setDonnees(serviceStats.compterParDomaine(articles));
        graphiqueAnnees.setDonnees(serviceStats.compterParAnnee(articles));

        repaint();
    }

    @Override
    public void effacer() {
        this.articles = java.util.Collections.emptyList();
        mettreAJour();
    }

    // ===================== GRAPHIQUE DOMAINES (barres) =====================
    class GraphiqueDomaines extends JPanel {
        private Map<String, Integer> donnees = new java.util.LinkedHashMap<>();

        public void setDonnees(Map<String, Integer> d) { this.donnees = d; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            setBackground(BLANC);

            if (donnees.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.ITALIC, 13));
                g2.drawString("Aucune donnée disponible", 50, getHeight() / 2);
                return;
            }

            int w = getWidth(), h = getHeight();
            int marge = 45, margeB = 60;
            int max = donnees.values().stream().mapToInt(v -> v).max().orElse(1);

            Color[] couleurs = {
                new Color(0, 84, 166), new Color(34, 139, 34), new Color(180, 100, 0),
                new Color(100, 60, 180), new Color(180, 30, 30), new Color(0, 150, 150),
                new Color(200, 130, 0)
            };

            java.util.List<Map.Entry<String, Integer>> entries =
                new java.util.ArrayList<>(donnees.entrySet()).subList(0,
                    Math.min(7, donnees.size()));

            int nbBarres = entries.size();
            int largeurBarre = (w - marge * 2) / nbBarres - 8;

            int idx = 0;
            for (Map.Entry<String, Integer> entry : entries) {
                int hauteur = (int) ((double) entry.getValue() / max * (h - marge - margeB));
                int x = marge + idx * ((w - marge * 2) / nbBarres);
                int y = h - margeB - hauteur;

                g2.setColor(couleurs[idx % couleurs.length]);
                g2.fillRoundRect(x, y, largeurBarre, hauteur, 6, 6);

                // Valeur au-dessus
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.drawString(String.valueOf(entry.getValue()), x + largeurBarre / 2 - 5, y - 4);

                // Label en bas (raccourci)
                g2.setColor(new Color(60, 60, 80));
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                String label = entry.getKey().length() > 10 ?
                    entry.getKey().substring(0, 10) + "." : entry.getKey();
                g2.drawString(label, x, h - margeB + 14);
                idx++;
            }

            // Axe X
            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(marge, h - margeB, w - marge, h - margeB);
        }

        @Override public Dimension getPreferredSize() { return new Dimension(300, 220); }
    }

    // ===================== GRAPHIQUE ANNÉES (ligne) =====================
    class GraphiqueAnnees extends JPanel {
        private Map<Integer, Integer> donnees = new java.util.TreeMap<>();

        public void setDonnees(Map<Integer, Integer> d) { this.donnees = d; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            setBackground(BLANC);

            if (donnees.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.ITALIC, 13));
                g2.drawString("Aucune donnée disponible", 50, getHeight() / 2);
                return;
            }

            int w = getWidth(), h = getHeight();
            int marge = 45, margeB = 40;
            int max = donnees.values().stream().mapToInt(v -> v).max().orElse(1);

            java.util.List<Map.Entry<Integer, Integer>> entries =
                new java.util.ArrayList<>(donnees.entrySet());
            int n = entries.size();
            if (n < 2) return;

            int[] xs = new int[n], ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = marge + i * (w - marge * 2) / (n - 1);
                ys[i] = h - margeB - (int)((double) entries.get(i).getValue() / max * (h - marge - margeB));
            }

            // Remplissage sous la courbe
            int[] polyX = new int[n + 2], polyY = new int[n + 2];
            for (int i = 0; i < n; i++) { polyX[i] = xs[i]; polyY[i] = ys[i]; }
            polyX[n] = xs[n-1]; polyY[n] = h - margeB;
            polyX[n+1] = xs[0]; polyY[n+1] = h - margeB;
            g2.setColor(new Color(0, 84, 166, 40));
            g2.fillPolygon(polyX, polyY, n + 2);

            // Ligne
            g2.setColor(new Color(0, 84, 166));
            g2.setStroke(new BasicStroke(2.5f));
            for (int i = 0; i < n - 1; i++) g2.drawLine(xs[i], ys[i], xs[i+1], ys[i+1]);

            // Points et labels
            for (int i = 0; i < n; i++) {
                g2.setColor(BLANC);
                g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
                g2.setColor(new Color(0, 84, 166));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(xs[i] - 5, ys[i] - 5, 10, 10);

                g2.setFont(new Font("Arial", Font.BOLD, 10));
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(String.valueOf(entries.get(i).getValue()), xs[i] - 4, ys[i] - 8);

                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                g2.setColor(new Color(80, 80, 100));
                g2.drawString(String.valueOf(entries.get(i).getKey()), xs[i] - 12, h - margeB + 14);
            }

            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(marge, h - margeB, w - marge, h - margeB);
        }

        @Override public Dimension getPreferredSize() { return new Dimension(300, 220); }
    }
}
