import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TwoThreadServer{
    private volatile boolean running = true;
    

   private void handleClient(Socket client,BufferedReader console){
    try {
        System.out.println("Connection is Established with "+client.getInetAddress());
        BufferedReader fromClient=new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter toClient=new PrintWriter(client.getOutputStream(),true);
        
       Thread reader=new Thread(()->{
           try {
               String messageFromClient;
               while ((messageFromClient=fromClient.readLine())!=null){
                   System.out.print("Client: ");
                   System.out.println(messageFromClient);
                   if(messageFromClient.equalsIgnoreCase("bye"))
                   {
                    System.out.println("Server Reader is shutting down..");
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
               String messageToClient;
               while (true) {
                   System.out.print("Type Your Message: ");
                   messageToClient=console.readLine();
                   toClient.println(messageToClient);
                   if(messageToClient.equalsIgnoreCase("Bye")){
                    System.out.println("Server Writer is shutting down..");
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

       client.close();




       writer.join();
       reader.join();


    } catch (Exception e) {
      e.printStackTrace();
    }
       
   }



   private void runServer(){
    int port=8010;
    BufferedReader console =new BufferedReader(new InputStreamReader(System.in));
    try(ServerSocket serverSocket=new ServerSocket(port)){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running = false;
                try {
                    serverSocket.close();
                } catch (IOException ignored) {}
            }));

      System.out.println("Waiting for Listner at.. "+port);
      while (running) {
        try{
        Socket client=serverSocket.accept();   
        handleClient(client,console);
        }
        catch(SocketException e){
                    System.out.println("Server socket closed, shutting down.");
                }
        catch(IOException e){
            System.err.println("I/O error on accept: " + e.getMessage());
        }
      }
    }
    catch(Exception e){
        e.printStackTrace();
    }
   }




    public static void main(String[] args) {
        TwoThreadServer server=new TwoThreadServer();
        server.runServer();
    }
}