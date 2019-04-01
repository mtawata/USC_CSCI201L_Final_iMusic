package servlets;

import java.sql.SQLException;

public class Register {
	public static boolean register(String username, String password, String name) throws SQLException, ClassNotFoundException {
		if(name.length() < 1 || name.length() > 50) {
			System.out.println("Returning false for name");
			return false;
		}
		
		if(username.length() > 25 || username.length() < 9) {
			System.out.println("Returning false for username");
			return false;
		}
		
		
		if(Database.userExists(username)) {
			System.out.println("Returning false for usernameexists");
			return false;
		}
		
		Database.addUser(username, password, name);
		return true;
	}
}
