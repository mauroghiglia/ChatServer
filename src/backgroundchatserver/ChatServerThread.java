/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backgroundchatserver;

/**
 *
 * @author MGhigl
 */
import java.net.*;
import java.io.*;
import backgroundchatserver.ChatMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServerThread extends Thread {
   private ChatServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;
   private ObjectOutputStream outObj;
   private ChatMessage chatMessage;

   public ChatServerThread(ChatServer server, Socket socket) {
      super();
      this.server = server;
      this.socket = socket;
      ID     = socket.getPort();
   }
   //Here the server receives the message.
   public void run() {
        System.out.println("Server Thread " + ID + " running.");
        chatMessage = new ChatMessage();
        while (true) {
            try {
                chatMessage = (ChatMessage) new ObjectInputStream(streamIn).readObject();;
                server.handle(ID, chatMessage);
            } catch(IOException ioe) {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
   
   //Here we send messages
   public void sendMessage(ChatMessage chatMessage) {
       try {
           ObjectOutputStream oos = new ObjectOutputStream(streamOut);
           oos.writeObject(chatMessage);
           oos.flush();

       } catch (IOException ex) {
           Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
    public int getID() {  
        return ID;
    }
    
    public void open() {
       try {
           streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
           streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
       } catch (IOException ex) {
           Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
       }
    }
    
    public void close() throws IOException {
        if (socket != null)    socket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }
}
