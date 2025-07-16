import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;


public class StudentLogin extends JFrame {
    private final String ISSUE_FILE = "resources/issued.csv";

    public StudentLogin(ArrayList<Student> studentsList, Dashboard dashboardRef) {
        setTitle("Student Login");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idField = new JTextField();
        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        panel.add(new JLabel("Enter Student ID:"));
        panel.add(idField);
        panel.add(new JLabel(""));
        panel.add(loginBtn);
        panel.add(new JLabel(""));
        panel.add(backBtn);

        add(panel, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> {
            String id = idField.getText().trim().toLowerCase();
            Student matched = null;
            for (Student s : studentsList) {
                if (s.id.toLowerCase().equals(id)) {
                    matched = s;
                    break;
                }
            }

            if (matched != null) {
                showStudentProfile(matched);
            } else {
                JOptionPane.showMessageDialog(this, "Student not found!");
            }
        });

        backBtn.addActionListener(e -> {
            this.dispose();
            dashboardRef.setVisible(true);
        });

        setVisible(true);
    }

    private void showStudentProfile(Student student) {
        JFrame profileFrame = new JFrame("Student Profile - " + student.name);
        profileFrame.setSize(650, 700);
        profileFrame.setLocationRelativeTo(null);
        profileFrame.setLayout(new BorderLayout(10, 10));

        JPanel detailPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        detailPanel.add(new JLabel("Student ID:"));     detailPanel.add(new JLabel(student.id));
        detailPanel.add(new JLabel("Name:"));           detailPanel.add(new JLabel(student.name));
        detailPanel.add(new JLabel("Father's Name:"));  detailPanel.add(new JLabel(student.father));
        detailPanel.add(new JLabel("Mother's Name:"));  detailPanel.add(new JLabel(student.mother));
        detailPanel.add(new JLabel("Stream:"));         detailPanel.add(new JLabel(student.stream));
        detailPanel.add(new JLabel("Year/Semester:"));  detailPanel.add(new JLabel(student.yearOrSem));
        detailPanel.add(new JLabel("Mobile:"));         detailPanel.add(new JLabel(student.phone));
        detailPanel.add(new JLabel("Email:"));          detailPanel.add(new JLabel(student.email));
        detailPanel.add(new JLabel("Address:"));        detailPanel.add(new JLabel(student.address));
        detailPanel.add(new JLabel("Photo:"));          detailPanel.add(new JLabel(new File(student.photoPath).getName()));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            profileFrame.dispose();
            this.setVisible(true);
        });

        JButton issueBtn = new JButton("📚 Issue Book");
        issueBtn.addActionListener(e -> issueBook(student.id));

        JButton returnBtn = new JButton("🔁 Return Book");
        returnBtn.addActionListener(e -> returnBook(student.id));

        JButton viewHistoryBtn = new JButton("📜 View My History");
        viewHistoryBtn.addActionListener(e -> showTextHistory(student.id));

        JButton viewTableBtn = new JButton("📖 View Issue Table");
        viewTableBtn.addActionListener(e -> showIssueHistory(student.id));

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(backBtn);
        bottomPanel.add(issueBtn);
        bottomPanel.add(returnBtn);
        bottomPanel.add(viewTableBtn);
        bottomPanel.add(viewHistoryBtn);

        profileFrame.add(detailPanel, BorderLayout.CENTER);
        profileFrame.add(bottomPanel, BorderLayout.SOUTH);

        profileFrame.setVisible(true);
        this.setVisible(false);
    }

    private void issueBook(String studentId) {
        JTextField bookCodeField = new JTextField();
        String issueDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Book Code:"));
        panel.add(bookCodeField);
        panel.add(new JLabel("Issue Date:"));
        panel.add(new JLabel(issueDate));

        int result = JOptionPane.showConfirmDialog(this, panel, "Issue Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ISSUE_FILE, true))) {
                bw.write(studentId + "," + bookCodeField.getText() + "," + issueDate + ",Pending,0");
                bw.newLine();
                JOptionPane.showMessageDialog(this, "✅ Book issued!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error issuing book!");
            }
        }
    }

    private void returnBook(String studentId) {
        JTextField bookCodeField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Book Code:"));
        panel.add(bookCodeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Return Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String code = bookCodeField.getText().trim();
            List<String[]> updatedLines = new ArrayList<>();
            boolean found = false;

            try (BufferedReader br = new BufferedReader(new FileReader(ISSUE_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[0].equals(studentId) && parts[1].equals(code) && parts[3].equals("Pending")) {
                        found = true;
                        String returnDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        Date issue = new SimpleDateFormat("dd-MM-yyyy").parse(parts[2]);
                        long diff = (new Date().getTime() - issue.getTime()) / (1000 * 60 * 60 * 24);
                        long fine = diff > 15 ? (diff - 15) * 2 : 0;
                        parts[3] = returnDate;
                        parts[4] = String.valueOf(fine);
                        updatedLines.add(parts);
                        JOptionPane.showMessageDialog(this, fine > 0 ? "Fine: ₹" + fine : "Returned successfully!");
                    } else {
                        updatedLines.add(parts);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error processing return!");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ISSUE_FILE))) {
                for (String[] parts : updatedLines) {
                    bw.write(String.join(",", parts));
                    bw.newLine();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving updates!");
            }

            if (!found) JOptionPane.showMessageDialog(this, "No pending issue found!");
        }
    }

    private void showIssueHistory(String studentId) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Student ID", "Book Code", "Issue Date", "Return Date", "Fine ₹"});

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(ISSUE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equalsIgnoreCase(studentId)) {
                    model.addRow(parts);
                    found = true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading issue records!");
            return;
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No issue history found!");
            return;
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 250));

        JOptionPane.showMessageDialog(this, scrollPane, "My Issue History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTextHistory(String studentId) {
        StringBuilder history = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(ISSUE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().startsWith(studentId.toLowerCase() + ",")) {
                    history.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading history!");
            return;
        }

        if (history.length() == 0) {
            JOptionPane.showMessageDialog(this, "No history found!");
            return;
        }

        JTextArea area = new JTextArea(history.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Raw History", JOptionPane.INFORMATION_MESSAGE);
    }
}

