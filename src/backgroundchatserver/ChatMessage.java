/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backgroundchatserver;

import java.io.Serializable;

/**
 *
 * @author MGhigl
 */
public class ChatMessage implements Serializable {
    
    private int msgType = 0;
    private String msg;
    private String ipAddress;
    private String toIPAddress;

    public ChatMessage() {
    }

    public ChatMessage(String msg) {
        this.msgType = 0;
        this.msg = msg;
    }
    
    //This constructor is mainly for system messages
    public ChatMessage(int msgType, String msg) {
        this.msgType = msgType;
        this.msg = msg;
    }
    
    //Constructor for IP exchange between server and client
    public ChatMessage(int msgType, String msg, String IPAddress) {
        this.msgType = msgType;
        this.msg = msg;
        this.ipAddress = IPAddress;
    }
    
    public ChatMessage(String msg, String ipAddress, String toIPAddress) {
        this.msg = msg;
        this.ipAddress = ipAddress;
        this.toIPAddress = toIPAddress;
    }
    
    public int getMsgType() {
        return msgType;
    }
    
    public void setMsgType(int type) {
        this.msgType = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getToIPAddress() {
        return toIPAddress;
    }

    public void setToIPAddress(String ToIPAddress) {
        this.toIPAddress = ToIPAddress;
    }

    
}