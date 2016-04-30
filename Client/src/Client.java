import java.io.*;
import java.net.*;

public class Client
{
	public static void main(String args[])
	{
		if(args.length != 1)
		{
			System.out.println("EchoClient pg3f4");
		}
	
		InputStreamReader convert = new InputStreamReader(System.in);
		BufferedReader stdin = new BufferedReader(convert);
		
		try
		{
			Socket echoClient = new Socket("localhost", 14560);
			PrintStream outs = new PrintStream(echoClient.getOutputStream());
			BufferedReader ins = new BufferedReader(new InputStreamReader(echoClient.getInputStream()));
			Thread listen = new Thread(){
				public void run(){
					String line2;
					while(true){
					try {
						line2 = ins.readLine();
						if(!(line2.equals(""))){
							System.out.println(line2);
						}
						if(line2.equals("Good Bye!")){
							echoClient.close();
							this.interrupt();
							break;
						} else {
							System.out.print(">");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				}
			};
			System.out.println("Connected...");
			
			listen.start();
			
			System.out.print(">");
			
			while(!(echoClient.isClosed())){
					//System.out.print(">");
					String line = stdin.readLine();
					outs.println(line);
					if(line.equals("quit")){
						break;
					}
			}
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
}
