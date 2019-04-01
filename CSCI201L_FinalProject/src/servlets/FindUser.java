package servlets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import classes.Song;
import classes.User;

public class FindUser {
	public static String findUser(String userSearch) {
		ResultSet foundUsers = Database.getUsers(userSearch);
		
		try {
			while(foundUsers.next()) {
				String username = foundUsers.getString("username");
				String password = foundUsers.getString("password");
				String name = foundUsers.getString("name");
				String bio = foundUsers.getString("bio");
				User tempUser = new User(username, password, name, bio, getUserSongs(foundUsers));
				
				return tempUser.userToJson();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ArrayList<Song> getUserSongs(ResultSet users) throws SQLException{
		String song = "";
		while(users.next()) {
			song = users.getString("songs");
		}
		
		String[] songs = song.split(",");
		ArrayList<Song> aSongs = new ArrayList<>();
		
		for(int i = 0; i < songs.length; i++) {
			Song temp = new Song(songs[i], filePathToName(songs[i]));
			aSongs.add(temp);
		}
		
		return aSongs;
	}
	
	public static String filePathToName(String filePath){
		return filePath.split("/")[filePath.split("/").length-1];
	}
}
