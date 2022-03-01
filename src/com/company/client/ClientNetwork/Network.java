package com.company.client.ClientNetwork;

import com.company.client.Listeners.MulticastListener;
import com.company.client.Listeners.TCPListenThread;
import com.company.client.Listeners.UDPListenThread;
import com.company.client.Printer.Printer;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Network {

    private final int portNumber = 12345;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final TCPListenThread listenThread;
    private final DatagramChannel ds;
    private String nick;
    private final UDPListenThread listenThreadUDP;
    private final MulticastListener multicastListener;
    private final String serverIp = "127.0.0.1";
    private final String multicastIp = "224.0.0.1";
    private final Charset coding = StandardCharsets.UTF_8;

    public Network(String ip, Printer printer) throws IOException {
        // TCP stuff
        socket = new Socket(serverIp, portNumber);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        listenThread = new TCPListenThread(printer, in);

        // UDP stuff
        ds = DatagramChannel.open();
        ds.bind(new InetSocketAddress(ip, portNumber));
        listenThreadUDP = new UDPListenThread(ip, ds, printer, null);

        // UDP multicast
        multicastListener = new MulticastListener(printer, portNumber, multicastIp);
    }

    public void setNick(String nick){
        this.nick = nick;
        this.listenThreadUDP.setNick(nick);
        this.multicastListener.setNick(nick);
    }

    public void close() {
        listenThread.stopMe();
        listenThreadUDP.stopMe();
        multicastListener.stopMe();
        try {
            listenThread.join();
            listenThreadUDP.join();
            multicastListener.join();
            ds.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendTCP(String msg){
        out.println(msg);
    }

    public String receiveTCP() throws IOException {
        return in.readLine();
    }

    public void startListen(){
        listenThread.start();
        listenThreadUDP.start();
        multicastListener.start();
    }

    public void sendUDP(String msg) throws IOException {
        msg = "B:"+nick+"#"+msg;
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(coding));
        InetSocketAddress serverAdress = new InetSocketAddress(serverIp, portNumber);
        ds.send(buffer, serverAdress);
        buffer.clear();
    }

    public void sendMulticast(String msg) throws IOException {
        msg = nick+": "+msg;
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(multicastIp);
        byte[] buf = msg.getBytes(coding);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, portNumber);
        socket.send(packet);
        socket.close();
    }
}
