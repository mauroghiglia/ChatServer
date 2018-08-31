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
import java.net.Socket;

/**
 *
 * @author MGhigl
 */
public class ConnectedClient {
    private int ID;
    private String ipAddress;
    private ChatServerThread chatServerThread;
    


    public ConnectedClient(ChatServerThread chatServerThread) {
//        this.ID = ID;
//        this.ipAddress = ipAddress;
        this.chatServerThread = chatServerThread;
    }

    public int getID() {
        return ID;
    }

    public ChatServerThread getChatServerThread() {
        return chatServerThread;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    
}
