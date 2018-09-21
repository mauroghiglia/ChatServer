/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backgroundchatserver;

/**
 *
 * @author MGhigl
 * Version 1.1.1
 */
public class BackgroundChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // START Chat Server
        ChatServer chatServer = new ChatServer(4444);
    }
    
}
