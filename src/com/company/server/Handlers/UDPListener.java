package com.company.server.Handlers;

import com.company.server.BroadcastManager;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPListener extends Thread{

    private final int portNumber;
    private final BroadcastManager broadcastManager;
    private boolean working = true;

    public UDPListener(int portNumber, BroadcastManager broadcastManager){
        this.portNumber = portNumber;
        this.broadcastManager = broadcastManager;
    }

    @Override
    public void run(){
        System.out.println("[UDP LISTENER][RUN] start listening");
        int byteBuffSize = 65535;
        try {
            DatagramChannel serverChannel = DatagramChannel.open();
            InetSocketAddress iAdd = new InetSocketAddress("127.0.0.1", 12345);
            serverChannel.bind(iAdd);
            while(true){
                ByteBuffer buffer = ByteBuffer.allocate(byteBuffSize);
                serverChannel.receive(buffer);
                buffer.flip();
                int limits = buffer.limit();
                byte bytes[] = new byte[limits];
                buffer.get(bytes, 0, limits);
                String msg = new String(bytes);

                if(!msg.contains("B:")){
                    continue;
                }
                msg = msg.replaceFirst("B:", "");
                System.out.println("[UDP LISTENER][RUN] received msg: "+msg);
                broadcastManager.UDPBroadcast(msg, serverChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMe(){
        this.working = false;
    }

}
