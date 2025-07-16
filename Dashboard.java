import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;


public class Dashboard extends JFrame {
    private JTextField idField, nameField, fatherField, motherField, phoneField, emailField, addressField;
    private JComboBox<String> streamBox, yearSemBox;
    private JLabel photoLabel;
    private String photoPath = "";
    private JTable table;
    private DefaultTableModel model;
    private JCheckBox darkModeCheck;
    private boolean isEditMode = false;
    private final java.util.Map<String, String> streamCodeMap = Map.of(
        "BCA", "BCA",
        "BA", "BA",
        "B.Sc.", "BSC",
        "B.ED", "BED",
        "BA + B.ED", "BABED",
        "B.Sc. + B.ED", "BSCBED",
        "B.Com.", "BCOM"
    );
    private ArrayList<Student> students = new ArrayList<>();
    private JButton okUpdateBtn;  


    public Dashboard() {
        setTitle("Student Record Management System");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

       
        addField(formPanel, gbc, "Student ID:", idField = new JTextField(), row, 0);
        addField(formPanel, gbc, "Name:", nameField = new JTextField(), row++, 2);

       
        addField(formPanel, gbc, "Father's Name:", fatherField = new JTextField(), row, 0);
        addField(formPanel, gbc, "Mother's Name:", motherField = new JTextField(), row++, 2);

        
        addField(formPanel, gbc, "Stream:", streamBox = new JComboBox<>(new String[]{
            "BCA", "BA", "B.Sc.", "B.ED", "BA + B.ED", "B.Sc. + B.ED", "B.Com."
        }), row, 0);
        addField(formPanel, gbc, "Year/Semester:", yearSemBox = new JComboBox<>(), row++, 2);

        streamBox.addActionListener(e -> {
            updateYearSem();
            if (!isEditMode) generateStudentID();
        });

        yearSemBox.addActionListener(e -> {
            if (!isEditMode) generateStudentID();
        });


        
        addField(formPanel, gbc, "Mobile Number:", phoneField = new JTextField(), row, 0);
        addField(formPanel, gbc, "Email ID:", emailField = new JTextField(), row++, 2);

        
        addField(formPanel, gbc, "Address:", addressField = new JTextField(), row++, 0);

       
        JButton uploadBtn = new JButton("Upload Photo");
        uploadBtn.addActionListener(e -> selectPhoto());
        gbc.gridx = 2;
        gbc.gridy = row;
        formPanel.add(uploadBtn, gbc);

        photoLabel = new JLabel("No Photo");
        gbc.gridx = 3;
        formPanel.add(photoLabel, gbc);

        
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.add(formPanel, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        String[] actions = {"Add", "Search", "Update", "Delete", "Show All", "Export CSV", "Export PDF", "Reset"};
        for (String text : actions) {
            JButton btn = new JButton(text);
            btn.setPreferredSize(new Dimension(120, 30));
            btn.addActionListener(e -> handleButton(text));
            buttonPanel.add(btn);
        }

        JButton studentLoginBtn = new JButton("Student Login");
        buttonPanel.add(studentLoginBtn);
        studentLoginBtn.addActionListener(e -> {
            this.setVisible(false); 
            new StudentLogin(FileHandler.loadAll(), this);
        });

        JButton libraryBtn = new JButton("Library");
        libraryBtn.addActionListener(e -> {
            this.setVisible(false); 
            new LibraryLogin();     
        });
        buttonPanel.add(libraryBtn);


        darkModeCheck = new JCheckBox("Dark Mode");
        darkModeCheck.addActionListener(e -> Utils.toggleDarkMode(this, darkModeCheck.isSelected()));
        buttonPanel.add(darkModeCheck);

        okUpdateBtn = new JButton("OK");
        okUpdateBtn.setVisible(false);
        okUpdateBtn.addActionListener(e -> performFinalUpdate());
        buttonPanel.add(okUpdateBtn);


       
        model = new DefaultTableModel();
        model.setColumnIdentifiers(Student.getCSVHeader().split(","));
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1150, 250));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Records"));

        
        add(formContainer, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        updateYearSem();
        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int y, int x) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = x + 1;
        panel.add(field, gbc);
    }

    private void updateYearSem() {
        String selected = streamBox.getSelectedItem().toString();
        yearSemBox.removeAllItems();
        if (selected.contains("+")) {
            for (int i = 1; i <= 4; i++) yearSemBox.addItem(i + " Year");
        } else if (selected.equals("B.ED")) {
            for (int i = 1; i <= 2; i++) yearSemBox.addItem(i + " Year");
        } else {
            for (int i = 1; i <= 6; i++) yearSemBox.addItem(i + " Sem");
        }
    }

    private void selectPhoto() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            photoPath = file.getAbsolutePath();
            photoLabel.setText(file.getName());
        }
    }

    private void handleButton(String action) {
        switch (action) {
            case "Add" -> addStudent();
            case "Search" -> searchStudent();
            case "Update" -> updateStudent();
            case "Delete" -> deleteStudent();
            case "Show All" -> loadAllStudents();
            case "Export CSV" -> FileHandler.exportCSV("resources/students.csv", model);
            case "Export PDF" -> FileHandler.exportPDF(model);
            case "Reset" -> resetForm();
        }
    }

    private void addStudent() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty() || FileHandler.idExists(id)) {
                JOptionPane.showMessageDialog(this, "Invalid or duplicate ID!");
                return;
            }
            String name = nameField.getText().trim();
            String father = fatherField.getText().trim();
            String mother = motherField.getText().trim();
            String stream = streamBox.getSelectedItem().toString();
            String yearSem = yearSemBox.getSelectedItem().toString();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            if (!Utils.isValidEmail(email) || !Utils.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(this, "Invalid Email or Mobile!");
                return;
            }
            Student s = new Student(id, name, father, mother, stream, yearSem, phone, email, address, photoPath);
            FileHandler.addStudent(s);
            JOptionPane.showMessageDialog(this, "Student Added!");
            loadAllStudents();
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid data in all fields!");
        }
    }

    private void searchStudent() {
        // Input fields
        JTextField idInput = new JTextField();
        JTextField nameInput = new JTextField();
        JComboBox<String> streamInput = new JComboBox<>(new String[]{
            "", "BCA", "BA", "B.Sc.", "B.ED", "BA + B.ED", "B.Sc. + B.ED", "B.Com."
        });
        JComboBox<String> yearSemInput = new JComboBox<>();

        streamInput.addActionListener(e -> {
            yearSemInput.removeAllItems();
            String selected = streamInput.getSelectedItem().toString();
            if (selected.contains("+")) {
                for (int i = 1; i <= 4; i++) yearSemInput.addItem(i + " Year");
            } else if (selected.equals("B.ED")) {
                for (int i = 1; i <= 2; i++) yearSemInput.addItem(i + " Year");
            } else if (!selected.isEmpty()) {
                for (int i = 1; i <= 6; i++) yearSemInput.addItem(i + " Sem");
            }
        });

        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Student ID:"));
        panel.add(idInput);
        panel.add(new JLabel("Name:"));
        panel.add(nameInput);
        panel.add(new JLabel("Stream:"));
        panel.add(streamInput);
        panel.add(new JLabel("Year/Semester:"));
        panel.add(yearSemInput);

        int choice = JOptionPane.showConfirmDialog(this, panel, "Search Students", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        String id = idInput.getText().trim().toLowerCase();
        String name = nameInput.getText().trim().toLowerCase();
        String stream = streamInput.getSelectedItem().toString();
        String yearSem = yearSemInput.getSelectedItem() == null ? "" : yearSemInput.getSelectedItem().toString();

        ArrayList<Student> matches = new ArrayList<>();
        ArrayList<Student> all = FileHandler.loadAll();

        for (Student s : all) {
            boolean match = true;

            if (!id.isEmpty() && !s.id.toLowerCase().equals(id)) match = false;
            if (!name.isEmpty() && !s.name.toLowerCase().contains(name)) match = false;
            if (!stream.isEmpty() && !s.stream.equalsIgnoreCase(stream)) match = false;
            if (!yearSem.isEmpty() && !s.yearOrSem.equalsIgnoreCase(yearSem)) match = false;

            if (match) matches.add(s);
        }

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching student found!");
            return;
        }

        
        DefaultTableModel tempModel = new DefaultTableModel();
        tempModel.setColumnIdentifiers(Student.getCSVHeader().split(","));
        for (Student s : matches) {
            tempModel.addRow(s.toCSV().split(","));
        }

        JTable tempTable = new JTable(tempModel);
        JScrollPane scrollPane = new JScrollPane(tempTable);
        scrollPane.setPreferredSize(new Dimension(1000, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }


    private void updateStudent() {
        JTextField idFieldInput = new JTextField();
        JTextField nameFieldInput = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Enter Student ID (optional):"));
        inputPanel.add(idFieldInput);
        inputPanel.add(new JLabel("Enter Name (optional):"));
        inputPanel.add(nameFieldInput);

        int choice = JOptionPane.showConfirmDialog(this, inputPanel, "Search Student to Update", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        String id = idFieldInput.getText().trim().toLowerCase();
        String name = nameFieldInput.getText().trim().toLowerCase();

        ArrayList<Student> matches = new ArrayList<>();
        for (Student s : FileHandler.loadAll()) {
            if ((!id.isEmpty() && s.id.toLowerCase().equals(id)) ||
                (!name.isEmpty() && s.name.toLowerCase().contains(name))) {
                matches.add(s);
            }
        }

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching student found!");
            return;
        }

        
        if (matches.size() == 1) {
            fillForm(matches.get(0));
            isEditMode = true;
            okUpdateBtn.setVisible(true); 
            JOptionPane.showMessageDialog(this, "Student found. You can edit and click OK to update.");
            return;
        }

        
        DefaultTableModel tempModel = new DefaultTableModel();
        tempModel.setColumnIdentifiers(Student.getCSVHeader().split(","));
        for (Student s : matches) {
            tempModel.addRow(s.toCSV().split(","));
        }

        JTable tempTable = new JTable(tempModel);
        JScrollPane scrollPane = new JScrollPane(tempTable);
        JButton editBtn = new JButton("Edit Selected");

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(editBtn, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Select Student to Edit", true);
        dialog.setSize(1000, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().add(dialogPanel);

        editBtn.addActionListener(e -> {
            int selectedRow = tempTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a student!");
                return;
            }

            Student selected = matches.get(selectedRow);
            fillForm(selected);
            isEditMode = true;
            okUpdateBtn.setVisible(true);
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Student selected. Now edit and press OK.");
        });

        dialog.setVisible(true);
    }


    private void deleteStudent() {
        JTextField idFieldInput = new JTextField();
        JTextField nameFieldInput = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Enter Student ID (optional):"));
        inputPanel.add(idFieldInput);
        inputPanel.add(new JLabel("Enter Name (optional):"));
        inputPanel.add(nameFieldInput);

        int choice = JOptionPane.showConfirmDialog(this, inputPanel, "Search Student to Delete", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        String id = idFieldInput.getText().trim().toLowerCase();
        String name = nameFieldInput.getText().trim().toLowerCase();

        ArrayList<Student> matches = new ArrayList<>();
        for (Student s : FileHandler.loadAll()) {
            if ((!id.isEmpty() && s.id.toLowerCase().equals(id)) ||
                (!name.isEmpty() && s.name.toLowerCase().contains(name))) {
                matches.add(s);
            }
        }

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching student found!");
            return;
        }

      
        if (matches.size() == 1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?\n" + matches.get(0).toCSV(), "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                FileHandler.deleteStudent(matches.get(0).id);
                JOptionPane.showMessageDialog(this, "Deleted successfully!");
                loadAllStudents();
            }
            return;
        }

      
        DefaultTableModel tempModel = new DefaultTableModel();
        tempModel.setColumnIdentifiers(Student.getCSVHeader().split(","));
        for (Student s : matches) {
            tempModel.addRow(s.toCSV().split(","));
        }

        JTable tempTable = new JTable(tempModel);
        JScrollPane scrollPane = new JScrollPane(tempTable);
        JButton deleteBtn = new JButton("Delete Selected");

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(deleteBtn, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Select Student to Delete", true);
        dialog.setSize(1000, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().add(dialogPanel);

        deleteBtn.addActionListener(e -> {
            int selectedRow = tempTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a student!");
                return;
            }

            Student selected = matches.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(dialog, "Delete this student?\n" + selected.toCSV(), "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                FileHandler.deleteStudent(selected.id);
                JOptionPane.showMessageDialog(dialog, "Deleted successfully!");
                loadAllStudents();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }


    private void loadAllStudents() {
        model.setRowCount(0);
        ArrayList<Student> list = FileHandler.loadAll();
        students = list;
        for (Student s : list) {
            model.addRow(s.toCSV().split(","));
        }

    }

    private void fillForm(Student s) {
        isEditMode = true;
        idField.setText(s.id);
        nameField.setText(s.name);
        fatherField.setText(s.father);
        motherField.setText(s.mother);
        streamBox.setSelectedItem(s.stream);
        updateYearSem();
        yearSemBox.setSelectedItem(s.yearOrSem);
        phoneField.setText(s.phone);
        emailField.setText(s.email);
        addressField.setText(s.address);
        photoPath = s.photoPath;
        photoLabel.setText(new File(photoPath).getName());
    }

    private void resetForm() {
        for (JTextField tf : new JTextField[]{
                idField, nameField, fatherField, motherField,
                phoneField, emailField, addressField
        }) tf.setText("");
        photoPath = "";
        photoLabel.setText("No Photo");

        isEditMode = false;
        generateStudentID(); 
    }

    private void generateStudentID() {
        String streamName = (String) streamBox.getSelectedItem();
        String yearSem = (String) yearSemBox.getSelectedItem();

        if (streamName == null || yearSem == null || yearSem.isBlank()) {
            idField.setText("");
            return;
        }

        String streamCode = streamCodeMap.get(streamName);
        if (streamCode == null) {
            idField.setText("");
            return;
        }

        ArrayList<Student> all = FileHandler.loadAll();
        int maxNum = 0;
        boolean groupMatched = false;

        for (Student s : all) {
            if (s.stream.equalsIgnoreCase(streamName) && s.yearOrSem.equalsIgnoreCase(yearSem)) {
                groupMatched = true;
                if (s.id.startsWith(streamCode)) {
                    try {
                        String numPart = s.id.substring(streamCode.length());
                        int num = Integer.parseInt(numPart);
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        if (groupMatched) {
            String newID = streamCode + String.format("%03d", maxNum + 1);
            idField.setText(newID);
        } else {
            idField.setText("");
        }
    }


    private void performFinalUpdate() {
        try {
            String id = idField.getText().trim();
            if (!FileHandler.idExists(id)) {
                JOptionPane.showMessageDialog(this, "Student ID doesn't exist!");
                return;
            }

            String name = nameField.getText().trim();
            String father = fatherField.getText().trim();
            String mother = motherField.getText().trim();
            String stream = streamBox.getSelectedItem().toString();
            String yearSem = yearSemBox.getSelectedItem().toString();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (!Utils.isValidPhone(phone) || (!email.isEmpty() && !Utils.isValidEmail(email))) {
                JOptionPane.showMessageDialog(this, "Invalid phone or email!");
                return;
            }

            Student s = new Student(id, name, father, mother, stream, yearSem, phone, email, address, photoPath);
            FileHandler.updateStudent(s);
            JOptionPane.showMessageDialog(this, "Student updated successfully!");
            loadAllStudents();
            resetForm();
            isEditMode = false;
            okUpdateBtn.setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid data!");
        }
    }

}

