package classes;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Song {
	private String filepath;
	private String user;
	private String songName;
	private String date;
	
	public Song() {
		
	}
	
	public Song(String user, String songName) {
		this.user = user;
		this.songName = songName;
	}
	
	public String getFilepath() {
		return filepath;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getSongNaem() {
		return songName;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setUser(String username) {
		user = username;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
}
