import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


public class SocketThread extends Thread {
	
	Socket s = null;
	BufferedReader ins;
	PrintStream outs;
	String line;
	String username;
	Boolean loggedIn = false;

	public SocketThread() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public SocketThread(Socket s) throws IOException{
		super();
		this.s = s;
		System.out.println("Client Connected");
	}

	public void run(){
		try {
			ins = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			outs = new PrintStream(s.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
		try {
			line = ins.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String words[] = line.split("\\s+");
		String result = "";
		
		
		
		if (words.length >= 1){
			switch(words[0]){
				case "login":
					if(words.length == 3){
						result = login(words[1], words[2]);
					} else {
						result = "Incorrect number of arguments. (\"login username password\")";
					}
					break;
				case "newuser":
					if(words.length == 3){
						result = newUser(words[1], words[2]);
					} else {
						result = "Incorrect number of arguments. (\"newuser username password\")";
					}
					break;
				case "logout":
					if(loggedIn){
						if(words.length == 1){
							result = logout();
						} else {
							result = "Incorrect number of arguments. (\"logout\")";
						}
					} else {
						result = "Please log in first.";
					}
					break;
				case "help":
					result = "Commands: newuser, login, send, who, logout, help";
					break;
				case "who":
					if(loggedIn){
						if(words.length == 1){
							result = who();	
						} else {
							result = "Incorrect number of arguments. (\"who\")";
						}
					} else {
						result = "Please log in first.";
					}
					break;
					
				case "send":
					if(loggedIn){
						try {
							if(words.length > 2){
								result = send(words[1], words);
							} else {
								result = "Please log in first.";
								result = "Incorrect number of arguments. (\"send user message\")";
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						result = "Please log in first.";
					}
					break;
				case "quit":
					result = "Good Bye!";
					if(loggedIn){
						logout();
					}
					break;
				default:
					result = "Incorrect Command";
					break;
			}
		}
		outs.println(result); 
		if(result.equals("Good Bye!")){
			try {
				s.close();
				System.out.println("Client Closed.");
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	}
	
	
	private String logout(){
		String username;
		if((username = Hashmaps.loggedInUsers.get(s)) != null){
			Hashmaps.loggedInUsers.remove(s);
			Hashmaps.loggedInSockets.remove(username);
			this.username = "";
			this.loggedIn = false;
			try {
				castToAll(username + " has left the chat room!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(username + " has left the chat room!"); 
			return "You are now logged out!";
		} else {
			return "You were not logged in!";
		}
	}
	
	private String login(String username, String password){
		if(Hashmaps.registeredUsers.containsKey(username)){
			if(Hashmaps.registeredUsers.get(username).equals(password)){
					if(Hashmaps.loggedInSockets.get(username) == null){
						Hashmaps.loggedInUsers.put(s, username);
						Hashmaps.loggedInSockets.put(username, s);
						this.username = username;
						this.loggedIn = true;
						try {
							castToAll(username + " has joined the chat room!");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(username + " has joined the chat room!"); 
						return "Logged In!";
					}
					else {
						return "This user is already logged in.";
					}
			} else {
				return "Wrong password.";
			}
		} else {
			return "Username is not registered.";
		}
	}
	
	private String newUser(String username, String password){
		if(Hashmaps.registeredUsernames.contains(username)){
			return "Username already exists.";
		}
		if(username.length() < 32){
			if(password.length() >= 4 && password.length() <= 8){
				Hashmaps.registeredUsers.put(username, password);
				Hashmaps.registeredUsernames.add(username);
				try(FileWriter fw = new FileWriter("src/accounts.txt", true);
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
					    out.println(username + ", " + password + ";");
					} catch (IOException e) {
					    e.printStackTrace();
					}
			} else {
				return "Password must be between 4 and 8 characters";
			}
		} else {
			return "Username must be under 32 characters";
		}
		System.out.println("User '" + username +"' has been added to the database!");
		return "User '" + username +"' has been added to the database!";
	}
	
	private String who(){
		Collection<String> c = Hashmaps.loggedInUsers.values();
		Iterator<String> i = c.iterator();
		String result = "";
		while(i.hasNext()){
			result += i.next() + ", ";
		}
		System.out.println(result);
		return result;
	}
	
	private String send(String user, String[] message) throws IOException{
		String spacedMessage = "";
		for(int i = 2; i < message.length; i++){
			spacedMessage += message[i] + " ";
		}
		if(user.equals("all")){
			castToAll(username + ": " + spacedMessage);
			System.out.println(username + " to all: " + spacedMessage);
		} else {
			Socket s;
			if((s = Hashmaps.loggedInSockets.get(user)) != null){
				PrintStream receiverOuts = new PrintStream(s.getOutputStream());
				receiverOuts.println(username + ": " + spacedMessage);
				System.out.println(username + " to " + user + ": " + spacedMessage);
			} else {
				return "User not logged in.";
			}
		}
		
		return "";
	}
	
	private void castToAll(String message) throws IOException{
		Collection<Socket> sockets = Hashmaps.loggedInSockets.values();
		Iterator<Socket> i = sockets.iterator();
		while(i.hasNext()){
			PrintStream outstream = new PrintStream(i.next().getOutputStream());
			outstream.println(message);
		}
	}

}
