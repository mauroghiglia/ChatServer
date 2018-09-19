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
        String IPAddress = client.getIpAddress();
        String destIP = "";
        String respMsg = "";
        
        if(chatMessage.getMsgType() == 0) {
//            System.out.println("Received a normal message of type: " 
//                    + chatMessage.getMsgType());
//            System.out.println("From IP: " + chatMessage.getIpAddress());
//            System.out.println("To IP: " + chatMessage.getToIPAddress());
//            System.out.println("Content: " + chatMessage.getMsg());
            
            Optional<ConnectedClient> destClient = connectedClients
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue()
                                            .getIpAddress()
                                            .equals(chatMessage
                                            .getToIPAddress()))
                                    .map(Map.Entry::getValue)
                                    .findFirst();
            try {
                destIP = destClient.get().getIpAddress();
                System.out.println("Destination IP " + destIP);
                ChatServerThread destThread = destClient.get()
                        .getChatServerThread();
                respMsg = "Message from:" + destIP + "\n\t" + chatMessage.getMsg();
                chatMessage.setMsg(respMsg);
                destThread.sendMessage(chatMessage);
            } catch(NoSuchElementException e) {
                System.out.println("This IP is not connected");
            }
        } else if(chatMessage.getMsgType() == 1) {
            switch(chatMessage.getMsg()) {
                case "HELLO":
                    System.out.println("Handshake: asking for IP...");
                    clientThread.sendMessage(new ChatMessage(1, "YOURIP"));
                    break;
                    
                case "MYIP":
                    System.out.println("User is sending IP address...");
                    client.setIpAddress(chatMessage.getIpAddress());
                    break;  
                    
                case "LIST":
                    System.out.println("User is requesting connected users list...");
                    System.out.println("Sending connected users list...");

                    connectedClients.forEach((k, v) -> {
                        v.getChatServerThread().sendMessage(new ChatMessage(1, "IPLIST", prepClientList(v.getIpAddress())));
                    });
//                    clientThread.sendMessage(new ChatMessage(1, "IPLIST", prepClientList(IPAddress)));
                    break;    
                    
                case "QUIT": 
                    System.out.println("User wants to quit...");
                    clientThread.sendMessage(new ChatMessage(1, "DISCONNECT"));
                    remove(ID);
                    break;
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
            
            //NEW: handshake process start
            System.out.println("Handshake start: Sending hello...");
            connectedClient.getChatServerThread().sendMessage(new ChatMessage(1, "HELLO")); 
        } else {
            System.out.println("Client refused: maximum " + maxConnections + " reached.");
        }
    }
    
    private String prepClientList(String currentIP) {
        StringBuilder list = new StringBuilder("Connected clients: \n");
        connectedClients.forEach((k, v) -> {
            System.out.println(k + " : " + v.getIpAddress());
            if(v.getIpAddress() == currentIP) {
                list.append(v.getIpAddress() + " (you)\n");
            } else {
                list.append(v.getIpAddress() + "\n");
            }
        });
        return list.toString();
    }
}
