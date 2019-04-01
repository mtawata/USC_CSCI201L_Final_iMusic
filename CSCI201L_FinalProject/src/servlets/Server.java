package servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/socket")
public class Server {

	private static Vector<Session> sessionVector = new Vector<Session>();
	private String musicFileName = "";
	boolean currentOperation = false;
	private static Vector<String> messages = new Vector<String>();
	private static Vector<Session> messageAuthors = new Vector<Session>();
	private static HashMap<Session,SessionThread> threads = new HashMap<Session,SessionThread>();
	
	public static Vector<String> getMessages() {
		return messages;
	}
	
	public static Vector<Session> getAuthors() {
		return messageAuthors;
	}
	
	public void addMessage(String message, Session session) {
		messages.add(message);
		messageAuthors.add(session);
	}
	
	FileOutputStream fso;
	
	
	public void makeNewFile(String fileName) {
		try {
			System.out.println("Making file: " + fileName);
			File songFile = new File(fileName);
			System.out.println("It's path is " + songFile.getAbsolutePath());
			fso = new FileOutputStream(songFile, true);
		} catch (FileNotFoundException e) {
			System.out.println("Error making the new file!");
			e.printStackTrace();
		}
	}
	

	@OnOpen
	public void open(Session session) {
		System.out.println("Connection made!");

		sessionVector.add(session);
		threads.put(session, new SessionThread(session));
	}


	//This is for song file uploads
	@OnMessage
	public void processUpload(byte[] b, boolean last, Session session) {
		
		if(!currentOperation) {
			makeNewFile(musicFileName);
		}
		
		//Write the file
		try {
			currentOperation = true;
			fso.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//If the file is done writing close the stream
		if (last) {
			try {
				currentOperation = false;
				fso.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@OnMessage
	public void onMessage(String message, Session session) throws IOException, EncodeException {
		// IF val := 0 --> login
		// IF val := 1 --> register
		// IF val := 2 --> search users
		// IF val := 3 --> store song
		// IF val := 4 --> loads uploaded songs for a specified user 
		// IF val := 5 --> sends a specified song (in binary) to the frontend for streaming
		
		System.out.println("message: " + message);
		int val = Integer.parseInt(message.substring(0, message.indexOf(',')));
		message = message.substring(message.indexOf(',') + 1);

		// If "0,username,password" in the message
		if (val == 0) {
			System.out.println(message);
			String username = message.substring(0, message.indexOf(','));
			String userPassword = message.substring(message.indexOf(',') + 1);
			try {
				try {
					String result = Login.login(username, userPassword);
					session.getBasicRemote().sendText(result);
					if (result.substring(2).equals("")) {
						addMessage(username + " logged in!", session);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("Issue on the server side of login");
				e.printStackTrace();
			}
		}
		// If we get a 1 we need "username,password,name" to be in the message
		else if (val == 1) {
			System.out.println(message);
			int commaLoc = message.indexOf(',');
			String username = message.substring(0, commaLoc);

			message = message.substring(commaLoc + 1);
			System.out.println(message);

			commaLoc = message.indexOf(',');
			String userPassword = message.substring(0, commaLoc);

			message = message.substring(commaLoc + 1);
			System.out.println(message);

			commaLoc = message.indexOf(',');
			String userPassword1 = message.substring(0, commaLoc);

			String name = message.substring(commaLoc + 1);

			if (!userPassword.equals(userPassword1)) {
				System.out.println("Passwords dont match! original " + userPassword + ", new: " + userPassword1);
				session.getBasicRemote().sendText("1,false");
			} else {
				try {
					String result = Boolean.toString(Register.register(username, userPassword, name));
					session.getBasicRemote().sendText("1," + result);
					if (result == "true") {
						addMessage(username + " just registered!", session);
					}
				} catch (ClassNotFoundException | SQLException e) {
					System.out.println("Error with register!");
					e.printStackTrace();
				}
			}
		}
		// we get a 2 we need "search" to be in the message
		else if (val == 2) {
			message = message.substring(1);
			session.getBasicRemote().sendText("2," + FindUser.findUser(message));
		}
		// passed in a string which represents a song
		// "3,username,songname"
		else if (val == 3) {
			musicFileName = "";
			
			String[] messageParts = message.split(",");
			String username = messageParts[0];
			musicFileName = messageParts[1];
			System.out.println("User: " + username + ", uploading: " + musicFileName);
			addMessage(username + " uploaded a song!", session);
			Database.addSong(username, musicFileName);
		}
		//4 Represents the frontend trying to load the song list for the user prepended with 4.
		//We will return a CSV formatted list of songs if the request was successful.
		//Else we will return "4,songsError!"
		//Needs "4,username"
		else if (val == 4) {
			String username = message;
			ArrayList<String> songs = Database.getSongs(username);
			if(songs == null || songs.size() == 0) {
				System.out.println("There was an error getting the user's songs!");
				session.getBasicRemote().sendText("4,songsError!");
			} else {
				String songsString = "";
				for(int i = 0; i < songs.size(); i++) {
					if(i < songs.size()-1) {
						songsString += songs.get(i) + ",";
					} else {
						songsString += songs.get(i);
					}
				}
				
				session.getBasicRemote().sendText("4," + songsString);
			}
		}
		//5 represents the frontend trying to get the song file specified by the arguments:
		//Needs: "5,username,songName"
		//We will return "5,Error!" if there was an error getting the song
		//Else we will return the song
		else if (val == 5) {
			String username = message.substring(0, message.indexOf(","));
			String songName = message.substring(message.indexOf(",") + 1);
			System.out.println("Got a 5!");
			System.out.println(new File(songName).getAbsolutePath());
			String stringPath = new File(songName).getAbsolutePath();
			
			if(stringPath == null || stringPath.length() == 0) {
				System.out.println("There was an error getting " + songName +"!");
				session.getBasicRemote().sendText("5,Error!");
			} else {
				Path path = Paths.get(stringPath);
				
				byte[] data = Files.readAllBytes(path);
				ByteBuffer song = ByteBuffer.wrap(data);
				addMessage(username + " began playing " + songName, session);
				
				session.getBasicRemote().sendBinary(song);
			}
		}
		
		//6 represents the frontend doing a search for a user 
		//Needs: "6, username"
		//Returns "6,Error!" if there was an error getting that user (user doesn't exist in database)
		//Else returns all information about the user in CSV format (name, username, bio, names of all songs separated by commas)
		//		If value for picture or bio is null in the database, the response contains the string "null" in its place
		else if (val == 6) {
			String username = message;
			try {
				if(!Database.userExists(username)) {
					session.getBasicRemote().sendText("6,Error!");
				}
				else {
					String responseText = Database.getName(username)+",";
					responseText += username + ",";
					responseText += Database.getBio(username) + ",";
					ArrayList<String> userSongs = Database.getSongs(username);
					
					for(String s: userSongs) {
						responseText += s+",";
					}
					
					responseText = responseText.substring(0, responseText.length());
					
					session.getBasicRemote().sendText("6,"+responseText);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		// 7 is front end home page rendering
		else if (val == 7) {
			String username = message;
			try {
				if(!Database.userExists(username)) {
					session.getBasicRemote().sendText("7,Error!");
				}
				else {
					String responseText = Database.getName(username)+",";
					responseText += username + ",";
					responseText += Database.getPicture(username) + ",";
					System.out.println(Database.getBio(username));
					responseText += Database.getBio(username) + ",";
					ArrayList<String> userSongs = Database.getSongs(username);
					
					for(String s: userSongs) {
						responseText += s+",";
					}
					
					responseText = responseText.substring(0, responseText.length());
					
					session.getBasicRemote().sendText("7,"+responseText);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		//Adding a bio
		else if (val == 8) {
			String bio = message;
			String[] bioParts = bio.split(",");
			bio = bioParts[1];
			String user = bioParts[0];
			
			try {
				Database.setBio(user, bio);
				addMessage(user + " updated their bio!", session);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		
		//user search
		else if(val == 9) {
			String usernameSearch = message;
			String resultString = "";
			try {
				ArrayList<String> matchingUsers = Database.findUsers(usernameSearch);
				
				for(String s: matchingUsers) {
					resultString += s + ",";
				}
				
				resultString = resultString.substring(0, resultString.length());
				session.getBasicRemote().sendText("9" + resultString);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	@OnClose
	public void close(Session session) {
		System.out.println("Disconnecting!");
		sessionVector.remove(session);
		threads.get(session).close();
		threads.remove(session);
	}

	@OnError
	public void error(Throwable error) {
		// System.out.println("Error!");
		System.out.println(error);
	}
}