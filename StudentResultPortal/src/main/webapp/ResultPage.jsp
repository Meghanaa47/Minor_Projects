<%@ page import="model.Student" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Student student = (Student) session.getAttribute("student");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> resultList = (List<Map<String, Object>>) session.getAttribute("result");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student Result</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f4f6f8;
            margin: 40px;
        }
        .container {
            background: #ffffff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            max-width: 800px;
            margin: auto;
        }
        h2, h3 {
            color: #333;
            border-bottom: 2px solid #ddd;
            padding-bottom: 5px;
        }
        table {
            width: 100%;
            margin-top: 20px;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid #ccc;
        }
        th, td {
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #f0f0f0;
        }
        .error {
            color: red;
            text-align: center;
            margin-top: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Student Result</h2>

    <%
        if (student == null) {
    %>
        <p class="error"> No result found or Invalid roll number.</p>
    <%
        } else {
    %>
       
        <table>
            <tr><th>Register Number</th><td><%= student.getRegno() %></td></tr>
            <tr><th>Name</th><td><%= student.getName() %></td></tr>
            <tr><th>Email</th><td><%= student.getEmail() %></td></tr>
            <tr><th>Department</th><td><%= student.getBranch() %></td></tr>
            <tr><th>Batch</th><td><%= student.getBatch() %></td></tr>
        </table>

        <%
            if (resultList != null && !resultList.isEmpty()) {
        %>
            <h3>Subject-wise Marks</h3>
            <table>
                <tr>
                    <th>Subject Name</th>
                    <th>Credits</th>
                    <th>Marks</th>
                    <th>Grade</th>
                </tr>
                <% for (Map<String, Object> row : resultList) { %>
                    <tr>
                        <td><%= row.get("subjectName") %></td>
                        <td><%= row.get("credits") %></td>
                        <td><%= row.get("marks") %></td>
                        <td><%= row.get("grade") %></td>
                    </tr>
                <% } %>
            </table>
            <h3>Overall Performance</h3>
            <table>
                <tr><th>Total Marks</th><td><%= session.getAttribute("totalMarks") %></td></tr>
                <tr><th>Percentage</th><td><%= session.getAttribute("percentage") %>%</td></tr>
                <tr><th>Overall Grade</th><td><%= session.getAttribute("overallGrade") %></td></tr>
            </table>
        <%
            } else {
        %>
            <p class="error">No subject-wise result found.</p>
        <%
            }
        }
    %>
</div>
</body>
</html>
