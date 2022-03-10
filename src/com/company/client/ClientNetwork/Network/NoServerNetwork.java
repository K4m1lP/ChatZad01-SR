package com.company.client.ClientNetwork.Network;

import com.company.client.ClientNetwork.Errors.Debug;
import com.company.client.Listeners.MulticastListener;
import com.company.client.Printer.Printer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NoServerNetwork {

    private final MulticastListener multicastListener;
    private String nick = null;
    private final String multicastIp = "224.0.0.1";
    private final Charset coding = StandardCharsets.UTF_8;
    private final int portNumber = 12345;

    public NoServerNetwork(Printer printer){
        if(Debug.debug)
            System.out.println("No server network constructor");
        multicastListener = new MulticastListener(printer, portNumber, multicastIp);
        multicastListener.start();
    }

    public void setNick(String nick){
        if(Debug.debug)
            System.out.println("Set nick to: " + nick);
        this.nick = nick;
        this.multicastListener.setNick(nick);
    }

    public void sendMulticast(String msg) throws IOException {
        if(nick == null)
            return;

        if(Debug.debug)
            System.out.println("Sending multicast, " + msg);

        msg = this.nick+": "+msg;
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(multicastIp);
        byte[] buf = msg.getBytes(coding);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, portNumber);
        socket.send(packet);
        socket.close();
    }

    public void close(){
        multicastListener.stopMe();
        nick = null;
    }

}
