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
import java.util.*;
import javafx.application.Platform;


public class ChatServer implements Runnable {
    private HashMap<Integer, ConnectedClient> connectedClients = new HashMap<>();
    private int maxConnections = 2;
    private ServerSocket serverSocket = null;
    private Thread listeningServerThread = null;
    
    public ChatServer(int port) {
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            serverSocket = new ServerSocket(port);  
            System.out.println("Server started on: " + serverSocket + " socket");
            start(); 
        } catch(IOException ioe) {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); 
        }
    }
    
    public void run() {
        while (listeningServerThread != null) {
            try { 
                System.out.println("Waiting for a client ..."); 
                addThread(serverSocket.accept()); 
            } catch(IOException ioe) {
                System.out.println("Server accept error: " + ioe); 
                stop(); 
            }
        }
    }

    public void start() {
        if (listeningServerThread == null) {
            listeningServerThread = new Thread(this); 
            listeningServerThread.start();
        }
    }
    
    /**
     * For now, we are stopping this thread with a stop method that's
     * deprecated as unsafe because, at this point, the entire 
     * program will stop and the thread is not holding any lock
     */
    
    public void stop() {
        if (listeningServerThread != null) {
            listeningServerThread.stop(); 
            listeningServerThread = null;
        }
    }
    
    public synchronized void handle(int ID, ChatMessage chatMessage) {
        ConnectedClient client = connectedClients.get(ID);
        ChatServerThread clientThread = client.getChatServerThread();
        String IPAddress = connectedClients.get(ID).getIpAddress();
        if (chatMessage.getMsg().equals(".bye")) {
            clientThread.sendMessage(new ChatMessage(".bye"));
            remove(ID); 
        } else  {
            switch(chatMessage.getMsgType()) {
            case 1  :   System.out.println("Got IP address...");
                        client.setIpAddress(chatMessage.getMsg());
                        break;
            default :   clientThread.sendMessage(new ChatMessage(IPAddress + ": " + chatMessage.getMsg()));
            }
        }
    }
    
    public synchronized void sendMessage(ChatMessage chatMessage, int ID) {
        connectedClients.get(ID).getChatServerThread().sendMessage(chatMessage);
    }
    
    public synchronized void remove(int ID) {
        ChatServerThread toTerminate = connectedClients.get(ID).getChatServerThread();
        System.out.println("Removing client thread " + ID);
        try {
            toTerminate.close(); 
        } catch(IOException ioe) {
            System.out.println("Error closing thread: " + ioe); 
        }
        toTerminate.stop(); 
        }
    
    private void addThread(Socket socket) {
        int ID = socket.getPort();
        ConnectedClient connectedClient = null;
        if(connectedClients.size() < maxConnections) {
            System.out.println("Client accepted: " + socket);
            connectedClient = new ConnectedClient(new ChatServerThread(this, socket));
            connectedClients.put(ID, connectedClient);
            connectedClient.getChatServerThread().open();
            connectedClient.getChatServerThread().start();
            System.out.println("Sending hello...");
            connectedClient.getChatServerThread().sendMessage(new ChatMessage("Hello from server...")); 
        } else {
            System.out.println("Client refused: maximum " + maxConnections + " reached.");
        }
        
    }
    
}
