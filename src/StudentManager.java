import java.sql.*;

public class StudentManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management"; // Database name
    private static final String USER = "root"; // Change if needed
    private static final String PASSWORD = ""; // Your MySQL password

    // Constructor - Ensures the table exists
    public StudentManager() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS student (" +
                "student_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, " + // student_id is UNSIGNED
                "name VARCHAR(100) NOT NULL, " +
                "gpa DECIMAL(10,2) UNSIGNED NOT NULL)"; // gpa is UNSIGNED
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add Student
    public void addStudent(Student student) {
        String sql = "INSERT INTO student (name, gpa) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getName());
            pstmt.setDouble(2, student.getGpa());
            pstmt.executeUpdate();

            // Get generated student_id from MySQL
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                student.setId(rs.getInt(1)); // Maps MySQL student_id to Java id
            }
            System.out.println("Student added: " + student.getName() + " (ID: " + student.getId() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // List Students
    public void listStudents() {
        String sql = "SELECT student_id, name, gpa FROM student"; // student_id in MySQL
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.isBeforeFirst()) {
                System.out.println("No students found.");
                return;
            }
            while (rs.next()) {
                int id = rs.getInt("student_id"); // Maps MySQL student_id to Java id
                String name = rs.getString("name");
                double gpa = rs.getDouble("gpa");
                System.out.println("ID: " + id + " | Name: " + name + " | GPA: " + gpa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update Student
    public void updateStudent(int id, String newName, double newGpa) {
        if (!studentExists(id)) {
            System.out.println("Student not found.");
            return;
        }
        String sql = "UPDATE student SET name = ?, gpa = ? WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setDouble(2, newGpa);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Student updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete Student
    public void deleteStudent(int id) {
        if (!studentExists(id)) {
            System.out.println("Student not found.");
            return;
        }
        String sql = "DELETE FROM student WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Student deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if Student Exists
    private boolean studentExists(int id) {
        String sql = "SELECT student_id FROM student WHERE student_id = ?"; // student_id in MySQL
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If result exists, return true
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
