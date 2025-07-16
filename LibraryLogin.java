import javax.swing.*;
import java.awt.*;

public class LibraryLogin extends JFrame {
    public LibraryLogin() {
        setTitle("Library Admin Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            this.dispose();
            new Dashboard(); 
        });
        add(backBtn, BorderLayout.SOUTH);

        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.equals("library") && password.equals("1234")) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                this.dispose();
                new LibraryDashboard(); 
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(new JLabel("")); add(loginBtn);

        setVisible(true);
    }
}

