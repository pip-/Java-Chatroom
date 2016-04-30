import java.io.*;
import java.net.*;
	
public class Server{

	//init-------------------------------
	static String accountsFile = "src/accounts.txt";
	static ServerSocket echoServer;
	//-----------------------------------

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			System.out.println("Waiting for a client to connect..."); 
			
			//init-------------------------------
			echoServer = new ServerSocket(14560);
			//Try not to use port number < 2000. 
			//-----------------------------------
			
			//Get users from file----------------
			File usersFilePath = new File(accountsFile);
			if(usersFilePath.canRead()){
				try (BufferedReader br = new BufferedReader(new FileReader(usersFilePath))){
					String line;
					while((line = br.readLine()) != null){
						line = line.trim();
						line = line.replaceAll(";", "");
						line = line.replaceAll(" ", "");
						String words[] = line.split(",");
						Hashmaps.registeredUsers.put(words[0], words[1]);
						Hashmaps.registeredUsernames.add(words[0]);
					}
					br.close();
				} catch(IOException e) {
					System.out.println(e);
				}
			}
			//-----------------------------------
			
			while (true)
			{
				Socket s = echoServer.accept();
				SocketThread t = new SocketThread(s);
				t.start();
		}
	} catch (IOException e){
		System.out.println(e);
		}
	}
}
