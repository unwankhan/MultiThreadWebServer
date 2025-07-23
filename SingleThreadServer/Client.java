
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{
     

     public void run(){
        int port=8010;
        String host="localhost";
        try(Socket clientSocket=new Socket(host,port);
              PrintWriter toServer=new PrintWriter(clientSocket.getOutputStream(),true);
              BufferedReader fromServer=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
              BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        )
        {
            System.out.println("Connected to server at " + host + ":" + port);
            while(true){
                 System.out.print("You: ");
                 String msgToServer=console.readLine();
                 toServer.println(msgToServer);
                 String msgFromServer=fromServer.readLine();
                 System.out.println(msgFromServer);
                 if(msgFromServer.equalsIgnoreCase("goodbye!"))
                 break;
            }
        }
        catch(Exception e){
            System.err.println("Client Error " + e.getMessage());
            e.printStackTrace();
        }

     }






    public static void main(String[] args) {
        Client client=new Client();
        try{
            client.run();
        }
        catch(Exception e){
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }






}