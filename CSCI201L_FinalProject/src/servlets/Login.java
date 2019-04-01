package servlets;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
	//If the server gets val = 0
	public static String login(String username, String userPassword) throws ClassNotFoundException {
		try {
			//If user does not exist return "username"
			ResultSet users = Database.getUsers(username);
			boolean usersExist = false;
			
			while(users.next()) {
				usersExist = true;
				break;
			}
			
			if(!usersExist) {
				return "0,username";
			}
			
			//If user's password is incorrect return "password"
			return Database.passwordMatch(username, users, userPassword);

		} catch (SQLException e) {
			System.out.println("Error login");
			e.printStackTrace();
		}
		
		return null;
	}
}
