import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class FileHandler {
    private static final String FILE_PATH = "resources/students.txt";

    public static void addStudent(Student s) {
        if (s == null) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(s.toCSV());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean idExists(String id) {
        ArrayList<Student> list = loadAll();
        for (Student s : list) {
            if (s.id.equals(id)) return true;
        }
        return false;
    }

    public static Student searchById(String id) {
        ArrayList<Student> list = loadAll();
        for (Student s : list) {
            if (s.id.equals(id)) return s;
        }
        return null;
    }

    public static boolean deleteStudent(String id) {
        ArrayList<Student> list = loadAll();
        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id.equals(id)) {
                list.remove(i);
                found = true;
                break;
            }
        }

        if (found) saveAll(list);
        return found;
    }

    public static void updateStudent(Student updated) {
        ArrayList<Student> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id.equals(updated.id)) {
                list.set(i, updated);
                break;
            }
        }
        saveAll(list);
    }

    public static ArrayList<Student> loadAll() {
        ArrayList<Student> list = new ArrayList<>();
        File f = new File(FILE_PATH);


        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Student s = Student.fromCSV(line);
                if (s != null) {
                    list.add(s);
                } else {
                    System.err.println("[CSV ERROR] Invalid student record: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<String[]> getIssuedBooks(String studentId) {
        ArrayList<String[]> books = new ArrayList<>();
        File file = new File("resources/book_issues.csv");
        if (!file.exists()) return books;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(studentId)) {
                    books.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }


    public static void saveAll(ArrayList<Student> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Student s : list) {
                writer.write(s.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void issueBook(String studentId, String bookCode, String issueDate) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("resources/book_issues.csv", true))) {
            bw.write(studentId + "," + bookCode + "," + issueDate);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean returnBook(String studentId, String bookCode) {
        File file = new File("resources/book_issues.csv");
        if (!file.exists()) return false;

        ArrayList<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(studentId) && parts[1].equals(bookCode)) {
                    found = true;
                    continue; 
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return found;
    }


    public static void exportCSV(String path, DefaultTableModel model) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.write(",");
            }
            writer.newLine();

          
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    writer.write(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 1) writer.write(",");
                }
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportPDF(DefaultTableModel model) {
        try {
            Document doc = new Document();
            String pdfPath = "resources/students.pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(pdfPath));
            doc.open();

            doc.add(new Paragraph("Student Records", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            doc.add(new Paragraph(" "));
            
            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());

            for (int i = 0; i < model.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(i)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(cell);
            }

           
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    pdfTable.addCell(new Phrase(model.getValueAt(i, j).toString()));
                }
            }

            doc.add(pdfTable);
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

