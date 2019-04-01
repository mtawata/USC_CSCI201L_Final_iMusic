package classes;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

public class User {

	private String username, password, name, bio;
	private List<Song> songs = new ArrayList<>();
	
	public User(String username, String password, String name, String bio, ArrayList<Song> songs) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.bio = "";
		this.songs = songs;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getBio() {
		return bio;
	}
	
	public void setBio(String content) {
		this.bio = content;
	}
	
	public List<Song> getSongs() {
		return songs;
	}
	
	public void addSong(Song song) {
		songs.add(song);
	}
	
	public String userToJson() {
		Gson gson = new Gson();
		String jsonString = gson.toJson(this);
		return jsonString;
	}
}
