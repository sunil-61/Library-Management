import java.util.ArrayList;

public class Student {
    String id, name, father, mother, stream, yearOrSem;
    String phone, email, address, photoPath;

    public Student(String id, String name, String father, String mother, String stream, String yearOrSem,
                   String phone, String email, String address, String photoPath) {
        this.id = id;
        this.name = name;
        this.father = father;
        this.mother = mother;
        this.stream = stream;
        this.yearOrSem = yearOrSem;
        this.phone = phone;
        this.email = (email == null || email.isBlank()) ? "N/A" : email;
        this.address = address;
        this.photoPath = (photoPath == null || photoPath.isBlank()) ? "N/A" : photoPath;
    }

    public String toCSV() {
        return String.join(",",
            escape(id), escape(name), escape(father), escape(mother), escape(stream), escape(yearOrSem),
            escape(phone), escapeOrBlank(email), escape(address), escapeOrBlank(photoPath)
        );
    }

    private String escape(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    private String escapeOrBlank(String value) {
        if (value == null || value.trim().isEmpty()) return "";
        return escape(value);
    }

    public static String getCSVHeader() {
        return "ID,Name,Father,Mother,Stream,Year/Sem,Phone,Email,Address,Photo";
    }

    public static Student fromCSV(String line) {
        ArrayList<String> partsList = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                partsList.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        partsList.add(sb.toString().trim());

        if (partsList.size() != 10) {
            System.err.println("[CSV ERROR] Invalid student record (wrong field count): " + line);
            return null;
        }

        return new Student(
            partsList.get(0), // ID
            partsList.get(1), // Name
            partsList.get(2), // Father
            partsList.get(3), // Mother
            partsList.get(4), // Stream
            partsList.get(5), // Year/Sem
            partsList.get(6), // Phone
            partsList.get(7), // Email 
            partsList.get(8), // Address
            partsList.get(9)  // PhotoPath 
        );
    }
}

