package com.company.client.Listeners;

import com.company.client.Printer.Printer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastListener extends Thread{

    private final Printer printer;
    private MulticastSocket socket = null;
    private byte[] buf = new byte[256];
    private boolean working = true;
    private String nick = null;
    private final int port;
    private final String multiIp;

    public MulticastListener(Printer printer, int port, String multiIp){
        this.printer = printer;
        this.port = port;
        this.multiIp = multiIp;
    }

    public void run() {
        try {
            socket = new MulticastSocket(port);
            InetAddress group = InetAddress.getByName(multiIp);
            socket.joinGroup(group);
            while (working) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if(received.split(":")[0].equals(nick) || received.contains("-U"))
                    continue;
                printer.printNewMsg(received);
            }
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException ignored) {}
    }

    public void stopMe(){
        this.working = false;
        this.socket.close();
    }

    public void setNick(String nick){
        this.nick = nick;
    }

}
