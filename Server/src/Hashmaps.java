import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Hashmaps {
		
		static ConcurrentHashMap<String, String> registeredUsers = new ConcurrentHashMap();
		static ConcurrentHashMap<Socket, String> loggedInUsers = new ConcurrentHashMap();
		static ConcurrentHashMap<String, Socket> loggedInSockets = new ConcurrentHashMap();
		static ArrayList<String> registeredUsernames = new ArrayList<String>();

		public Hashmaps() {
			// TODO Auto-generated constructor stub
			
		}

	}