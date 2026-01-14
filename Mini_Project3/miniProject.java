package Mini_Project3;

import java.sql.*;
import java.util.Scanner;

public class miniProject {

    static final String URL = "jdbc:mysql://localhost:3306/studentdb";
    static final String USER = "root";
    static final String PASS = "Saurabh@123";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        if (!login()) {
            System.out.println("Invalid Login! Exiting...");
            return;
        }

        int choice;
        do {
            System.out.println("\n----- STUDENT MANAGEMENT SYSTEM -----");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student by Eno");
            System.out.println("4. Update Student Branch");
            System.out.println("5. Delete Student by Eno");
            System.out.println("6. Display Sorted Students");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1: addStudent(); break;
                case 2: displayAll(); break;
                case 3: searchByEno(); break;
                case 4: updateBranch(); break;
                case 5: deleteByEno(); break;
                case 6: displaySorted(); break;
                case 7: System.out.println("Thank you!"); break;
                default: System.out.println("Invalid choice!");
            }

        } while (choice != 7);
    }

    static boolean login() {
        System.out.print("Username: ");
        String u = sc.next();
        System.out.print("Password: ");
        String p = sc.next();

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps =
                     con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {

            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Login Error");
            return false;
        }
    }
    
    static void addStudent() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            System.out.print("Eno: ");
            int eno = sc.nextInt();

            PreparedStatement check =
                    con.prepareStatement("SELECT eno FROM Students WHERE eno=?");
            check.setInt(1, eno);
            if (check.executeQuery().next()) {
                throw new Exception("Eno must be unique");
            }

            sc.nextLine();
            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Branch: ");
            String branch = sc.nextLine();
            if (branch.isEmpty())
                throw new Exception("Branch cannot be empty");

            System.out.print("Semester: ");
            int sem = sc.nextInt();

            System.out.print("Percentage: ");
            float per = sc.nextFloat();
            if (per <= 0)
                throw new Exception("Percentage must be positive");

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Students VALUES (?,?,?,?,?)");
            ps.setInt(1, eno);
            ps.setString(2, name);
            ps.setString(3, branch);
            ps.setInt(4, sem);
            ps.setFloat(5, per);

            ps.executeUpdate();
            System.out.println("Student added successfully");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void displayAll() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Students")) {

            while (rs.next()) {
                System.out.println(
                        rs.getInt(1) + " " +
                        rs.getString(2) + " " +
                        rs.getString(3) + " " +
                        rs.getInt(4) + " " +
                        rs.getFloat(5));
            }

        } catch (Exception e) {
            System.out.println("Display error");
        }
    }

    static void searchByEno() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter Eno: ");
            int eno = sc.nextInt();

            PreparedStatement ps =
                    con.prepareStatement("SELECT * FROM Students WHERE eno=?");
            ps.setInt(1, eno);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                System.out.println("Found: " + rs.getString("name"));
            else
                System.out.println("Student not found");

        } catch (Exception e) {
            System.out.println("Search error");
        }
    }

    static void updateBranch() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            System.out.print("Eno: ");
            int eno = sc.nextInt();
            sc.nextLine();

            System.out.print("New Branch: ");
            String branch = sc.nextLine();
            if (branch.isEmpty())
                throw new Exception("Branch cannot be empty");

            PreparedStatement ps =
                    con.prepareStatement("UPDATE Students SET branch=? WHERE eno=?");
            ps.setString(1, branch);
            ps.setInt(2, eno);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Updated successfully" : "Student not found");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void deleteByEno() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Eno: ");
            int eno = sc.nextInt();

            PreparedStatement ps =
                    con.prepareStatement("DELETE FROM Students WHERE eno=?");
            ps.setInt(1, eno);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Deleted" : "Student not found");

        } catch (Exception e) {
            System.out.println("Delete error");
        }
    }

    static void displaySorted() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement st = con.createStatement();
             ResultSet rs =
                     st.executeQuery("SELECT * FROM Students ORDER BY percentage DESC")) {

            while (rs.next()) {
                System.out.println(
                        rs.getInt(1) + " " +
                        rs.getString(2) + " " +
                        rs.getFloat(5));
            }

        } catch (Exception e) {
            System.out.println("Sort error");
        }
    }
}
