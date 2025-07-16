# Student Management System (Java Swing App)

This is a Java-based Student Management System and Library Management System combined into one desktop application using Swing GUI.

## 📦 Features

- Student Dashboard with:
  - Add, Update, Delete Student
  - Search by ID or Name
  - Export to PDF
  - Upload Photo
  - Auto Calculate Percentages
  - Dark Theme Toggle

- Student Login:
  - View own profile
  - Issue/Return Books
  - View Issue History
  - Clear Fine

- Library Dashboard (Admin Login):
  - Add/Remove Books
  - Book Search
  - Issue/Return books with fine calculation
  - Export Book List to PDF
  - Student-wise issue history

## 🛠️ Technologies Used

- Java 8+
- Swing (GUI)
- iTextPDF 5.5.13 (for PDF export)
- CSV file storage
- JAR packaging

## 🔐 Library Admin Login

- **Username:** `library`
- **Password:** `1234`

## 🚀 How to Run

### 🖥️ Run via Command Line (Linux / Windows / Mac):

```bash
javac -cp ".:lib/itextpdf-5.5.13.jar" *.java
java -cp ".:lib/itextpdf-5.5.13.jar" Main

## 🚀 How to Run

### 🖥️ Run via Command Line (Windows):

```bash
javac -cp ".;lib/itextpdf-5.5.13.jar" *.java
java -cp ".;lib/itextpdf-5.5.13.jar" Main