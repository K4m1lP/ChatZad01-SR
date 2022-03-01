package com.company.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class BroadcastManager {

    private Map<String, PrintWriter> queue;
    private Map<String, InetAddress> UDPqueue;

    public BroadcastManager(){
        queue = new HashMap<>();
        UDPqueue = new HashMap<>();
    }

    public void addClient(String nick, PrintWriter out, InetAddress ip){
        System.out.println("[BroadcastManager][ADD CLIENT] new client, nick: "+nick);
        queue.put(nick, out);
        UDPqueue.put(nick, ip);
    }

    public boolean isNickInUse(String nick){
        return queue.containsKey(nick);
    }

    public synchronized void TCPBroadcast(String nick, String msg){
        System.out.println("[BroadcastManager][TCPBroadcast] sending TCP, nick: "+nick+" msg: "+msg);
        queue.forEach((keyNick, out) ->{
            if(!keyNick.equals(nick))
                out.println(nick + ": " + msg);
        });
    }

    public void removeClient(String nick){
        System.out.println("[BroadcastManager][removeClient] removing client, nick: "+nick);
        queue.remove(nick);
    }

    public void UDPBroadcast(String inputMsg, DatagramChannel ds){
        System.out.println("[BroadcastManager][UDPBroadcast] broadcasting msg: "+inputMsg);
        ByteBuffer buffer = ByteBuffer.wrap(inputMsg.getBytes());
        InetSocketAddress serverAddress = new InetSocketAddress("255.255.255.255", 12345);
        try {
            ds.send(buffer, serverAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer.clear();
    }
}
