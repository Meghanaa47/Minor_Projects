package dao;
import java.sql.*;
import java.util.*;

import model.Student;
public class StudentDAO {
    private static Connection con;
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "meghana47");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateOtp(String regno, String otp) {
        try {
            String sql = "UPDATE students SET otp=? WHERE rollno=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, otp);
            ps.setString(2, regno);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Student getStudentByRegno(String regno) {
        Student student = null;
        try {
            String sql = "SELECT rollno, name, dept, email, batch, otp FROM students WHERE rollno=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, regno);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                student = new Student();
                student.setRegno(rs.getString("rollno"));
                student.setName(rs.getString("name"));
                student.setBranch(rs.getString("dept"));
                student.setEmail(rs.getString("email"));
                student.setBatch(rs.getString("batch"));
                student.setOtp(rs.getString("otp"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return student;
    }
    public static List<Map<String, Object>> getStudentMarks(String regno) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            String sql = "SELECT m.subid, s.sub_name, s.credits, m.marks, m.grade " +
                         "FROM marks m JOIN subjects s ON m.subid = s.sub_id " +
                         "WHERE m.rollno = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, regno);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("subid", rs.getString("subid"));
                map.put("subjectName", rs.getString("sub_name"));

                map.put("credits", rs.getInt("credits"));
                map.put("marks", rs.getInt("marks"));
                map.put("grade", rs.getString("grade"));
                resultList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
    public static int getTotalMarks(String regno) {
        int total = 0;
        try {
            String sql = "SELECT SUM(marks) AS total_marks FROM marks WHERE rollno = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, regno);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total_marks");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
    public static double getPercentage(String regno) {
        double percentage = 0.0;
        try {
            String sql = "SELECT SUM(marks) AS total, COUNT(*) AS count FROM marks WHERE rollno=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, regno);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int count = rs.getInt("count");
                if (count > 0) {
                    percentage = (total / (count * 100.0)) * 100;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Math.round(percentage * 100.0) / 100.0;
    }
    public static String getOverallGrade(String regno) {
        double perc = getPercentage(regno);
        if (perc >= 90) return "A+";
        else if (perc >= 80) return "A";
        else if (perc >= 70) return "B+";
        else if (perc >= 60) return "B";
        else if (perc >= 50) return "C";
        else if (perc >= 40) return "P";
        else return "F";
    }
}
