package servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Student;
import dao.StudentDAO;

@WebServlet("/otpServlet")
public class OtpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String regno = req.getParameter("roll");
		String otp = req.getParameter("otp");
		String action = req.getParameter("action");

		HttpSession session = req.getSession();
		session.setAttribute("roll", regno);
		if ("Send OTP".equals(action)) {
			Student student = StudentDAO.getStudentByRegno(regno);

			if (student != null) {
				String generatedOtp = String.valueOf(new Random().nextInt(900000) + 100000);
				StudentDAO.updateOtp(regno, generatedOtp);
				student.setOtp(generatedOtp);

				System.out.println("Generated OTP: " + generatedOtp);

				session.setAttribute("success", "OTP sent to your registered email: " + student.getEmail());
				
				session.setAttribute("otpSent", true);
			} else {
				session.setAttribute("error", "Student not found.");
			}
			res.sendRedirect("Home.jsp");

		} else if ("Verify OTP".equals(action)) {
			Student student = StudentDAO.getStudentByRegno(regno);
			if (student != null && student.getOtp() != null && student.getOtp().equals(otp)) {
				session.setAttribute("success", "OTP Verified Successfully!");
				session.setAttribute("student", student);
				List<Map<String, Object>> result = StudentDAO.getStudentMarks(regno);
                int total=StudentDAO.getTotalMarks(regno);
				session.setAttribute("result", result);
                session.setAttribute("totalMarks", total);
                double percent=StudentDAO.getPercentage(regno);
                session.setAttribute("percentage", percent);
                String grade=StudentDAO.getOverallGrade(regno);
                session.setAttribute("overallGrade",grade);
				res.sendRedirect("ResultPage.jsp");
			} else {
				session.setAttribute("error", "Invalid OTP. Please try again.");
				session.setAttribute("otpSent", true);
				res.sendRedirect("Home.jsp");
			}
		}
	}
}
