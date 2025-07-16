
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LibraryDashboard extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private final String BOOK_FILE = "resources/books.csv";

    public LibraryDashboard() {
        setTitle("Library Book Management");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Date", "Author", "Title", "Price", "Book Code"});
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));

        JButton addBtn = new JButton("Add Book");
        JButton removeBtn = new JButton("Remove Book");
        JButton refreshBtn = new JButton("Show All");
        JButton searchBtn = new JButton("Search Book");
        JButton exportBtn = new JButton("Export to PDF");
        JButton backBtn = new JButton("Back");

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(backBtn);

        addBtn.addActionListener(e -> addBookDialog());
        removeBtn.addActionListener(e -> removeBookDialog());
        refreshBtn.addActionListener(e -> loadBooks());
        searchBtn.addActionListener(e -> searchBookDialog());
        exportBtn.addActionListener(e -> exportToPDF());
        backBtn.addActionListener(e -> {
            this.dispose();
            new LibraryLogin();
        });

        add(buttonPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadBooks();
        setVisible(true);
    }

    private void addBookDialog() {
        JTextField dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        JTextField authorField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField codeField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Date:")); panel.add(dateField);
        panel.add(new JLabel("Author:")); panel.add(authorField);
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Price:")); panel.add(priceField);
        panel.add(new JLabel("Book Code:")); panel.add(codeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String[] book = {dateField.getText(), authorField.getText(), titleField.getText(), priceField.getText(), codeField.getText()};
            if (isBookCodeExists(book[4])) {
                JOptionPane.showMessageDialog(this, "Book code already exists!");
                return;
            }
            saveBook(book);
            loadBooks();
        }
    }

    private void removeBookDialog() {
        String code = JOptionPane.showInputDialog(this, "Enter Book Code to Delete:");
        if (code != null && !code.trim().isEmpty()) {
            deleteBook(code.trim());
            loadBooks();
        }
    }

    private void searchBookDialog() {
        String keyword = JOptionPane.showInputDialog(this, "Enter title, author or code to search:");
        if (keyword == null || keyword.trim().isEmpty()) return;

        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains(keyword.toLowerCase())) {
                    model.addRow(line.split(","));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error searching books!");
        }
    }

    private void exportToPDF() {
        JOptionPane.showMessageDialog(this, "PDF Export feature coming soon!");
    }

    private void saveBook(String[] book) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOK_FILE, true))) {
            bw.write(String.join(",", book));
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving book!");
        }
    }

    private void loadBooks() {
        model.setRowCount(0);
        File file = new File(BOOK_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) model.addRow(line.split(","));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading books!");
        }
    }

    private void deleteBook(String bookCode) {
        File file = new File(BOOK_FILE);
        if (!file.exists()) return;

        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.endsWith("," + bookCode)) lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading books!");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) bw.write(l + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving updated book list!");
        }
    }

    private boolean isBookCodeExists(String code) {
        File file = new File(BOOK_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[4].equals(code)) return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}

