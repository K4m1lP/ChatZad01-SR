package com.company.client.ClientNetwork.Network;

import com.company.client.ClientNetwork.Errors.NO_RECEIVE_ERROR;
import com.company.client.ClientNetwork.Errors.NO_SERVER_CONNECTION;
import com.company.client.Listeners.TCPListenThread;
import com.company.client.Listeners.UDPListenThread;
import com.company.client.Printer.Printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerNetwork {

    private final int portNumber = 12345;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final TCPListenThread listenThread;
    private final DatagramChannel ds;
    private String nick;
    private final UDPListenThread listenThreadUDP;
    private final String serverIp = "127.0.0.1";
    private final Charset coding = StandardCharsets.UTF_8;


    public ServerNetwork(String ip, Printer printer) throws NO_SERVER_CONNECTION {
        try {
            socket = new Socket(serverIp, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            listenThread = new TCPListenThread(printer, in);

            ds = DatagramChannel.open();
            ds.bind(new InetSocketAddress(ip, portNumber));
            listenThreadUDP = new UDPListenThread(ip, ds, printer, null);
        } catch (IOException e) {
            close();
            throw new NO_SERVER_CONNECTION();
        }
    }

    public void sendTCP(String msg){
        out.println(msg);
    }

    public void startListening(){
        listenThread.start();
        listenThreadUDP.start();
    }

    public String receiveTCP() throws NO_RECEIVE_ERROR {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new NO_RECEIVE_ERROR();
        }
    }

    public void sendUDP(String msg) throws IOException {
        msg = "B:"+nick+"#"+msg;
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(coding));
        InetSocketAddress serverAddress = new InetSocketAddress(serverIp, portNumber);
        ds.send(buffer, serverAddress);
        buffer.clear();
    }

    public void close(){
        try {
            if(socket!=null)
                socket.close();
            if(out!=null)
                out.close();
            if(in!=null)
                in.close();
            if(ds!=null)
                ds.close();
            nick = null;
            if(listenThread!=null){
                listenThread.stopMe();
                assert socket != null;
                socket.close();
                listenThread.join();
            }
            if(listenThreadUDP!=null){
                listenThreadUDP.stopMe();
                assert ds != null;
                ds.close();
                listenThreadUDP.join();
            }
        } catch (IOException ignored) {} catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setNick(String nick) {
        this.nick = nick;
        this.listenThreadUDP.setNick(nick);
    }
}
