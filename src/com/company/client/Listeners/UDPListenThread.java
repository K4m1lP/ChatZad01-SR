package com.company.client.Listeners;

import com.company.client.Printer.Printer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPListenThread extends Thread{

    private boolean working = true;
    private final String ip;
    private final DatagramChannel ds;
    private String nick;
    private final Printer printer;

    public UDPListenThread(String ip, DatagramChannel ds, Printer printer, String nick){
        this.ip = ip;
        this.nick = nick;
        this.ds = ds;
        this.printer = printer;
    }

    @Override
    public void run(){
        int byteBuffSize = 65535;
        while (working){
            ByteBuffer buffer = ByteBuffer.allocate(byteBuffSize);
            try {
                ds.receive(buffer);
                buffer.flip();
                int limits = buffer.limit();
                byte[] bytes = new byte[limits];
                buffer.get(bytes, 0, limits);
                String msg = new String(bytes);
                if(msg.contains("#")){
                    String[] tmp = msg.split("#");
                    if(!tmp[0].equals(nick)) {
                        printer.printNewMsg(tmp[0] + ": " + tmp[1]);
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    public void stopMe(){
        this.working = false;
    }

    public void setNick(String nick){
        this.nick = nick;
    }
}
