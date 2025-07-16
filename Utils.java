import javax.swing.*;
import java.awt.*;

public class Utils {

    public static void toggleDarkMode(Component root, boolean dark) {
        if (root == null) return;

       
        if (root instanceof Container container) {
            for (Component c : container.getComponents()) {
                toggleDarkMode(c, dark);
            }
        }

        if (dark) {
            safeSetBackground(root, Color.DARK_GRAY);
            safeSetForeground(root, Color.WHITE);
        } else {
            safeSetBackground(root, UIManager.getColor("Panel.background"));
            safeSetForeground(root, UIManager.getColor("Label.foreground"));
        }

        
        if (root instanceof JTextField tf) {
            tf.setCaretColor(dark ? Color.WHITE : Color.BLACK);
        }

        if (root instanceof JTable table) {
            table.setBackground(dark ? new Color(40, 40, 40) : Color.WHITE);
            table.setForeground(dark ? Color.WHITE : Color.BLACK);
            table.setGridColor(dark ? Color.GRAY : Color.LIGHT_GRAY);
            table.getTableHeader().setBackground(dark ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            table.getTableHeader().setForeground(dark ? Color.WHITE : Color.BLACK);
        }

        root.repaint();
    }

    private static void safeSetBackground(Component c, Color color) {
        try {
            if (color != null) c.setBackground(color);
        } catch (Exception ignored) {}
    }

    private static void safeSetForeground(Component c, Color color) {
        try {
            if (color != null) c.setForeground(color);
        } catch (Exception ignored) {}
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}

