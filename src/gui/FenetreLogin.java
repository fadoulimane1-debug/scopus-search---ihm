package gui;

import services.ServiceAuth;
import services.ServiceNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Fenêtre de connexion / inscription.
 */
public class FenetreLogin extends JDialog {

    private ServiceAuth         serviceAuth;
    private ServiceNotification serviceNotif;
    private boolean             connecte = false;

    private static final Color BLEU  = new Color(0, 84, 166);
    private static final Color BLANC = Color.WHITE;

    public FenetreLogin(ServiceAuth serviceAuth, ServiceNotification serviceNotif) {
        this.serviceAuth  = serviceAuth;
        this.serviceNotif = serviceNotif;
        setTitle("🔐 Scopus Search — Connexion");
        setSize(420, 480);
        setLocationRelativeTo(null);
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construireUI();
    }

    private void construireUI() {
        JPanel panneau = new JPanel(new BorderLayout());
        panneau.setBackground(BLANC);

        // --- En-tête ---
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU);
        entete.setBorder(new EmptyBorder(25, 20, 25, 20));
        JLabel titre = new JLabel("🔬 Scopus Search", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 22));
        titre.setForeground(BLANC);
        JLabel sous = new JLabel("Plateforme de Recherche Scientifique", SwingConstants.CENTER);
        sous.setFont(new Font("Arial", Font.PLAIN, 12));
        sous.setForeground(new Color(180, 210, 255));
        entete.add(titre, BorderLayout.CENTER);
        entete.add(sous, BorderLayout.SOUTH);
        panneau.add(entete, BorderLayout.NORTH);

        // --- Onglets Login / Inscription ---
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("Arial", Font.BOLD, 13));
        onglets.addTab("  🔑 Connexion  ",    panneauConnexion());
        onglets.addTab("  ✏️ Inscription  ",  panneauInscription());
        panneau.add(onglets, BorderLayout.CENTER);

        // --- Info demo ---
        JLabel info = new JLabel("  Compte démo : admin@scopus.com / 1234", SwingConstants.LEFT);
        info.setFont(new Font("Arial", Font.ITALIC, 11));
        info.setForeground(new Color(120, 120, 120));
        info.setBorder(new EmptyBorder(5, 10, 5, 10));
        panneau.add(info, BorderLayout.SOUTH);

        setContentPane(panneau);
    }

    private JPanel panneauConnexion() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BLANC);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        JTextField champEmail = new JTextField("admin@scopus.com");
        JPasswordField champMdp = new JPasswordField("1234");
        JLabel labelErreur = new JLabel(" ");
        labelErreur.setForeground(Color.RED);
        labelErreur.setFont(new Font("Arial", Font.ITALIC, 12));

        ajouterChamp(p, gbc, 0, "📧 Email :", champEmail);
        ajouterChamp(p, gbc, 1, "🔒 Mot de passe :", champMdp);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        p.add(labelErreur, gbc);

        JButton btnConnexion = creerBouton("🔑 Se connecter", BLEU);
        gbc.gridy = 3;
        p.add(btnConnexion, gbc);

        btnConnexion.addActionListener(e -> {
            String email = champEmail.getText().trim();
            String mdp   = new String(champMdp.getPassword());
            if (serviceAuth.connecter(email, mdp)) {
                serviceNotif.envoyerNotification("Connexion réussie",
                    "Bienvenue " + serviceAuth.getUtilisateurActuel().getNom() + " !", "SUCCES");
                connecte = true;
                dispose();
            } else {
                labelErreur.setText("❌ Email ou mot de passe incorrect !");
            }
        });

        return p;
    }

    private JPanel panneauInscription() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BLANC);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JTextField champNom   = new JTextField();
        JTextField champEmail = new JTextField();
        JPasswordField champMdp = new JPasswordField();
        JPasswordField champMdp2 = new JPasswordField();
        JLabel labelErreur = new JLabel(" ");
        labelErreur.setForeground(Color.RED);
        labelErreur.setFont(new Font("Arial", Font.ITALIC, 12));

        ajouterChamp(p, gbc, 0, "👤 Nom complet :", champNom);
        ajouterChamp(p, gbc, 1, "📧 Email :", champEmail);
        ajouterChamp(p, gbc, 2, "🔒 Mot de passe :", champMdp);
        ajouterChamp(p, gbc, 3, "🔒 Confirmer :", champMdp2);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        p.add(labelErreur, gbc);

        JButton btnInscrire = creerBouton("✅ Créer mon compte", new Color(34, 139, 34));
        gbc.gridy = 5;
        p.add(btnInscrire, gbc);

        btnInscrire.addActionListener(e -> {
            String nom  = champNom.getText().trim();
            String email = champEmail.getText().trim();
            String mdp  = new String(champMdp.getPassword());
            String mdp2 = new String(champMdp2.getPassword());

            if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
                labelErreur.setText("❌ Tous les champs sont obligatoires !");
            } else if (!mdp.equals(mdp2)) {
                labelErreur.setText("❌ Les mots de passe ne correspondent pas !");
            } else if (serviceAuth.inscrire(nom, email, mdp)) {
                serviceNotif.envoyerNotification("Compte créé", "Bienvenue " + nom + " !", "SUCCES");
                connecte = true;
                dispose();
            } else {
                labelErreur.setText("❌ Cet email est déjà utilisé !");
            }
        });

        return p;
    }

    private void ajouterChamp(JPanel p, GridBagConstraints gbc, int row, String label, JComponent champ) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        champ.setPreferredSize(new Dimension(180, 30));
        champ.setFont(new Font("Arial", Font.PLAIN, 12));
        p.add(champ, gbc);
    }

    private JButton creerBouton(String texte, Color fond) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(fond);
        btn.setForeground(BLANC);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public boolean isConnecte() { return connecte; }
}
