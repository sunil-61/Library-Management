import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class StudentBookPanel extends JFrame {
    private final String studentId;
    private JTable issuedTable;
    private DefaultTableModel issuedModel;

    public StudentBookPanel(String studentId) {
        this.studentId = studentId;
        setTitle("Student Book Panel - ID: " + studentId);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout());

        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Book");
        JButton reissueBtn = new JButton("Reissue Book");
        JButton backBtn = new JButton("Back");

        topPanel.add(issueBtn);
        topPanel.add(returnBtn);
        topPanel.add(reissueBtn);
        topPanel.add(backBtn);

        add(topPanel, BorderLayout.NORTH);

       
        issuedModel = new DefaultTableModel(new String[]{"Book Code", "Issue Date", "Days", "Fine"}, 0);
        issuedTable = new JTable(issuedModel);
        add(new JScrollPane(issuedTable), BorderLayout.CENTER);

        loadIssuedBooks();

        issueBtn.addActionListener(e -> openIssueDialog());
        returnBtn.addActionListener(e -> returnBook());
        reissueBtn.addActionListener(e -> reissueBook());
        backBtn.addActionListener(e -> {
            this.dispose();
            new Dashboard(); 
        });

        setVisible(true);
    }

    private void openIssueDialog() {
        String bookCode = JOptionPane.showInputDialog(this, "Enter Book Code to Issue:");
        if (bookCode == null || bookCode.trim().isEmpty()) return;

        ArrayList<String[]> issued = FileHandler.getIssuedBooks(studentId);
        for (String[] entry : issued) {
            if (entry[1].equals(bookCode)) {
                JOptionPane.showMessageDialog(this, "Already issued this book!");
                return;
            }
        }

        LocalDate today = LocalDate.now();
        FileHandler.issueBook(studentId, bookCode, today.toString());
        JOptionPane.showMessageDialog(this, "Book issued for 15 days (until " + today.plusDays(15) + ")");
        loadIssuedBooks();
    }

    private void returnBook() {
        int row = issuedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to return.");
            return;
        }

        String bookCode = issuedModel.getValueAt(row, 0).toString();
        String issueDateStr = issuedModel.getValueAt(row, 1).toString();
        LocalDate issueDate = LocalDate.parse(issueDateStr);
        long days = ChronoUnit.DAYS.between(issueDate, LocalDate.now());

        int fine = 0;
        if (days > 15) fine = (int)(days - 15) * 2;

        if (FileHandler.returnBook(studentId, bookCode)) {
            JOptionPane.showMessageDialog(this, "Book returned.\nFine: ₹" + fine);
            loadIssuedBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Error returning book.");
        }
    }

    private void reissueBook() {
        int row = issuedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to reissue.");
            return;
        }

        String bookCode = issuedModel.getValueAt(row, 0).toString();
        if (FileHandler.returnBook(studentId, bookCode)) {
            LocalDate today = LocalDate.now();
            FileHandler.issueBook(studentId, bookCode, today.toString());
            JOptionPane.showMessageDialog(this, "Book reissued for 15 more days.");
            loadIssuedBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Error reissuing book.");
        }
    }

    private void loadIssuedBooks() {
        issuedModel.setRowCount(0);
        ArrayList<String[]> books = FileHandler.getIssuedBooks(studentId);

        for (String[] entry : books) {
            String bookCode = entry[1];
            String issueDateStr = entry[2];
            LocalDate issueDate = LocalDate.parse(issueDateStr);
            long days = ChronoUnit.DAYS.between(issueDate, LocalDate.now());
            int fine = (days > 15) ? (int)(days - 15) * 2 : 0;

            issuedModel.addRow(new Object[]{bookCode, issueDateStr, days, "₹" + fine});
        }
    }
}

