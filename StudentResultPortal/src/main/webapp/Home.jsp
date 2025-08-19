<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>iResults - Home</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f8;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .container {
            background: #fff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            width: 400px;
        }
        h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 30px;
        }
        label {
            font-weight: bold;
            color: #34495e;
        }
        input[type="text"] {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 6px;
            box-sizing: border-box;
        }
        input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #2980b9;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
        input[type="submit"]:hover {
            background-color: #1c5980;
        }
        p {
            text-align: center;
            font-weight: bold;
            margin-top: 15px;
        }
        .error {
            color: red;
        }
        .success {
            color: green;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Student Result Portal</h2>
        <form method="post" action="otpServlet">
            <label>Register Number:</label>
            <input type="text" name="roll" placeholder="Enter roll no.." required
                   value="<%= (session.getAttribute("roll") != null) ? session.getAttribute("roll") : "" %>" />
            <% if(session.getAttribute("otpSent") == null) { %>
                <input type="submit" name="action" value="Send OTP"/>
            <% } else { %>
                <label>OTP:</label>
                <input type="text" name="otp" placeholder="Enter OTP.." required/>
                <input type="submit" name="action" value="Verify OTP"/>
            <% } %>
        </form>
        <%
            String error = (String) session.getAttribute("error");
            String success = (String) session.getAttribute("success");
            if(error != null){
        %>
            <p class="error"><%= error %></p>
        <%
            session.removeAttribute("error");
            } else if(success != null){
        %>
            <p class="success"><%= success %></p>
        <%
            session.removeAttribute("success");
            }
            session.removeAttribute("otpSent");
        %>
    </div>
</body>
</html>
