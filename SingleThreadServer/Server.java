
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;

public class Server{

   private volatile boolean running = true;

    public static void handleClient(Socket client){
        try {
             System.out.println("Connection is Established "+client.getRemoteSocketAddress());
             PrintWriter toClient=new PrintWriter(client.getOutputStream(),true);
             BufferedReader fromClient=new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
             String msgFromClient;


              while((msgFromClient=fromClient.readLine())!=null){
                System.out.println("Client: " +msgFromClient);
                if(msgFromClient.equalsIgnoreCase("bye")){
                    toClient.println("Goodbye!");
                    break;
                }
                else{
                    System.out.print("Server: ");
                   String messageToClient=console.readLine();
                     toClient.println(messageToClient);
                }
               
              }
        } catch (Exception e) {
            System.out.println("Something Wrong with I/O"+e.getMessage());
            e.printStackTrace();
        }
        finally{
            try{
                client.close();
            }
            catch(IOException e){
                System.out.println("Connection Closed"+e.getMessage());
                e.printStackTrace();
            }
        }
             
    }



    public void run(){


      int port=8010;
      try (ServerSocket server=new ServerSocket(port)){
          System.out.println("Server Listing on port "+port);


          Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running = false;
                try {
                    server.close();
                } catch (IOException ignored) {}
            }));



             while(running){
                try{
                    Socket client=server.accept();
                    handleClient(client);
                }
                catch(ServerException e){
                    System.out.println("Server socket closed, shutting down.");
                }
                catch(IOException e){
                    System.err.println("I/O error on accept: " + e.getMessage());
                }
             }

          
      } catch (Exception e) {
        System.err.println("I/O error: " + e.getMessage());
        e.printStackTrace();
      }
    }





    public static void main(String[] args) {
        Server server=new Server();
        try {
            server.run();
        } catch (Exception e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }




}