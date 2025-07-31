import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TwoThreadClient{
   private void handleServer(Socket server,BufferedReader console){

    try {
        System.out.println("Connection is Established with "+server.getInetAddress());
        BufferedReader fromServer=new BufferedReader(new InputStreamReader(server.getInputStream()));
        PrintWriter toServer=new PrintWriter(server.getOutputStream(),true);
        
       Thread reader=new Thread(()->{
           try {
               String messageFromServer;
               while ((messageFromServer=fromServer.readLine())!=null){
                   System.out.print("Server: ");
                   System.out.println(messageFromServer);
                   if(messageFromServer.equalsIgnoreCase("bye"))
                   {
                    System.out.println("Client Reader is shutting down..");
                   break;
                   }
               }
           } catch (IOException e) {
            e.printStackTrace();
               throw new RuntimeException(e);
           }


       }
       );

       Thread writer=new Thread(()->{
           try {
               String messageToServer;
               while (true) {
                   System.out.print("Type Your Message: ");
                   messageToServer=console.readLine();
                   toServer.println(messageToServer);
                   if(messageToServer.equalsIgnoreCase("Bye")){
                    System.out.println("Client Writer is shutting down..");
                   break;
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
               throw new RuntimeException(e);
           }
       });

       writer.start();
       reader.start();
       while(writer.isAlive()&&reader.isAlive()) {try{Thread.sleep(50);} catch(InterruptedException ignore){}}

       server.close();
       writer.join();
       reader.join();


    } catch (Exception e) {
      e.printStackTrace();
    }

   }





   private void callServer(){
    BufferedReader console =new BufferedReader(new InputStreamReader(System.in));
    String host="localhost";
    int port=8010;
    try{
        Socket clientSocket=new Socket(host,port);
        System.out.println("Connected to server at " + host + ":" + port);
        handleServer(clientSocket,console);
    }
    catch(Exception e){
       e.printStackTrace();
    }

   }
    
    public static void main(String[] args) {
        TwoThreadClient client=new TwoThreadClient();
        client.callServer();
    }
}