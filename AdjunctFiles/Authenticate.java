package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Authenticate")
public class Authenticate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String url="jdbc:mysql://localhost/csci201";
			String userName="root";
			String dbPassword="password";
			
			Connection conn = DriverManager
					.getConnection(url,userName, dbPassword);
			Statement statement = conn.createStatement();
			
			PrintWriter pw = response.getWriter();
			
			String username = request.getParameter("username");
			String userPassword = request.getParameter("password");
			
			ResultSet resultSet = statement.executeQuery("SELECT * from users where username='"+username+"'");
			
			String pass = "";
			boolean usersExist = false;
			
			while(resultSet.next()) {
				pass = resultSet.getString("password");
				usersExist = true;
			}
			
			
			if(usersExist) {
				if(pass.equals(userPassword)) {
					request.getSession().setAttribute("currentUser", username);
					pw.println("success");
				} else {
					pw.println("password");
				}
			} else {
				pw.println("username+password");
			}
			
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		

	}

}

