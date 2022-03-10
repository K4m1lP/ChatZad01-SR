package com.company.server.Handlers;

import com.company.server.BroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPUserThread extends Thread{

    private PrintWriter out;
    private BufferedReader in;
    private final BroadcastManager broadcastManager;
    private String nick = null;
    private boolean working = true;
    private final InetAddress ip;

    public TCPUserThread(Socket socket, BroadcastManager broadcastManager){
        ip = socket.getInetAddress();
        this.broadcastManager = broadcastManager;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNick() throws IOException {
        while (nick==null){
            String msg = in.readLine();
            if(msg!=null) {
                if (broadcastManager.isNickInUse(msg)) {
                    out.println("NOT_ACK");
                } else {
                    out.println("ACK");
                    broadcastManager.addClient(msg, out, ip);
                    nick = msg;
                    broadcastManager.TCPBroadcast(nick, "just joined.");
                }
            } else {
                out.println("NOT_ACK");
            }
        }
    }

    @Override
    public void run(){
        try {
            getNick();
        } catch (IOException e) {
            return;
        }
        String msg;
        while(working){
            try {
                msg = in.readLine();
                if(msg.equals("EXIT")){
                    broadcastManager.removeClient(nick);
                    broadcastManager.TCPBroadcast(nick, "went out.");
                    return;
                } else {
                    broadcastManager.TCPBroadcast(nick, msg);
                }
            } catch (IOException e) {
                broadcastManager.removeClient(nick);
                broadcastManager.TCPBroadcast(nick, "went out.");
                return;
            }
        }
    }

}
