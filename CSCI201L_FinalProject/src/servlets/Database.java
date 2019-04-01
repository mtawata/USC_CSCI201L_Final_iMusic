package servlets;

import java.sql.Connection;
import classes.Song;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {

	private static Statement connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost/imusic";
		String userName = "root";
		String dbPassword = "password";

		Connection conn = DriverManager.getConnection(url, userName, dbPassword);
		return conn.createStatement();
	}

	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost/imusic";
		String userName = "root";
		String dbPassword = "password";

		return DriverManager.getConnection(url, userName, dbPassword);
	}

	public static ResultSet getUsers(String username) {
		try {
			ResultSet resultSet = connect().executeQuery("SELECT * from users where username='" + username + "'");

			return resultSet;

		} catch (Exception e) {
			System.out.println("Issues getting users");
			e.printStackTrace();
		}

		return null;
	}

	// returns the filepath to the user's
//	public static ResultSet getPicture(String username) {
//		try {
//			ResultSet resultSet = connect().executeQuery("SELECT picture from users where username='" + username + "'");
//
//			return resultSet;
//		} catch (Exception e) {
//			System.out.println("Issues getting users");
//			e.printStackTrace();
//		}
//		return null;
//	}

	public static String passwordMatch(String username, ResultSet rs, String userPassword)
			throws SQLException, ClassNotFoundException {
		String pass = "";
		String name = "";

		rs = connect().executeQuery("SELECT * from users where username='" + username + "'");

		while (rs.next()) {
			pass = rs.getString("password");
		}

		if (pass.equals(userPassword)) {
			while (rs.next()) {
				name = rs.getString("name");
			}

			return "0," + name;
		} else {
			System.out.println("Got password: " + pass);
			System.out.println("Entered: " + userPassword);
			return "0,password";
		}
	}

	public static boolean userExists(String username) throws SQLException {
		ResultSet rs = getUsers(username);
		while (rs.next()) {
			return true;
		}

		return false;
	}

	public static void addUser(String username, String password, String name)
			throws SQLException, ClassNotFoundException {
		try {
			PreparedStatement ps = getConnection()
					.prepareStatement("INSERT INTO users (username, password, name) VALUES ('" + username + "','"
							+ password + "','" + name + "');");

			ps.executeUpdate();

		} catch (Exception e) {
			System.out.println("Issues adding user");
			e.printStackTrace();
		}
	}

	public static void addSong(String username, String songName) {
		try {
			ResultSet rs = connect().executeQuery("SELECT * from imusic.users where username='" + username + "'");
			songName += ",";
			while (rs.next()) {
				songName += rs.getString("songs");
			}
			
			System.out.println("Username = " + username);
			
			PreparedStatement ps = getConnection().prepareStatement(
					"UPDATE imusic.users SET songs = '" + songName + "' WHERE username = '" + username + "';");
			ps.executeUpdate();
			System.out.println("The song has been added to the database!");

		} catch (Exception e) {
			System.out.println("Issues adding a song!");
			e.printStackTrace();
		}
	}
	
	//This should be used on the home page. It will return a list of songs for the user.
	public static ArrayList<String> getSongs(String username) {
		ResultSet rs;
		ArrayList<String> songs;
		try {
			songs = new ArrayList<>();
			rs = connect().executeQuery("SELECT * from users where username='" + username + "'");
			String songString = "";
			while (rs.next()) {
				songString += rs.getString("songs");
			}
			String[] songsArray = songString.split(",");
			for (String s : songsArray) {
				songs.add(s);
			}
			
			return songs;
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Errors getting " + username + "'s songs!");
			e.printStackTrace();
		}
		
		return null;

	}
	
	//returns the bio of the specified user
	public static String getBio(String username) throws ClassNotFoundException, SQLException {
		ResultSet rs;
		String bio = "";
		rs = connect().executeQuery("SELECT * from users where username='" + username + "'");
		
		while (rs.next()) {
			bio = rs.getString("bio");
		}
		
		if(bio == null) {
			return "null";
		}
		
		return bio;
	}
	
	//Adds to the bio for the specified user
	public static void setBio(String username, String bioText) throws ClassNotFoundException, SQLException {
		try {
			PreparedStatement ps = getConnection().prepareStatement(
					"UPDATE users SET bio = '" + bioText + "' WHERE username = '" + username + "';");
			ps.executeUpdate();
			System.out.println("The bio text has been added to the database!");

		} catch (Exception e) {
			System.out.println("Issues adding bio text");
			e.printStackTrace();
		}
	}
	
	public static String getName(String username) throws ClassNotFoundException, SQLException {
		ResultSet rs;
		String name = "";
		rs = connect().executeQuery("SELECT * from users where username='" + username + "'");
		
		while (rs.next()) {
			name = rs.getString("name");
		}
		
		return name;
	}
	
	//returns the picture of the specified user
	public static String getPicture(String username) throws SQLException, ClassNotFoundException {
		ResultSet rs;
		String picture = "";
		rs = connect().executeQuery("SELECT * from users where username='" + username + "'");
		
		while (rs.next()) {
			picture = rs.getString("picture");
		}
		
		if(picture == null) {
			return "null";
		}
		
		return picture;
	}
	
	//returns all users with 'username' as a substring of their name
	public static ArrayList<String> findUsers(String username) throws SQLException, ClassNotFoundException {
		ResultSet rs;
		ArrayList<String> users = new ArrayList<String>();		
		rs = connect().executeQuery("SELECT * from users");
		while(rs.next()) {
			String currUser = rs.getString("username");
			if(currUser.contains(username)) 
				users.add(currUser);
		}		
		return users;
	}

	//returns all songs with 'song' as a substring of its songname
	public static ArrayList<Song> findSongs(String song) throws SQLException, ClassNotFoundException {
		ResultSet rs;
		ArrayList<Song> songs = new ArrayList<Song>();
		rs = connect().executeQuery("SELECT * from users");
		while(rs.next()) {
			Song s = new Song();
			s.setUser(rs.getString("username"));
			String allSongs = rs.getString("songs");
			if(allSongs.contains(song)) {
				int secondComma = allSongs.indexOf(',', allSongs.indexOf(song));
				int firstComma = -1;
				int currIndex = allSongs.indexOf(song);
				while(currIndex != -1) {
					if(allSongs.charAt(currIndex) == ',') {
						firstComma = currIndex;
						break;
					}
					currIndex--;
				}		
				s.setSongName((allSongs.substring(firstComma+1, secondComma)));
			}
		}
		return songs;
	}
}





