package com.company.client.Listeners;

import com.company.client.Printer.Printer;

import java.io.BufferedReader;
import java.io.IOException;

public class TCPListenThread extends Thread{

    private final BufferedReader in;
    private boolean working = true;
    private final Printer printer;

    public TCPListenThread(Printer printer, BufferedReader in){
        this.in = in;
        this.printer = printer;
    }

    @Override
    public void run() {
        while(working){
            try {
                String newMsg = in.readLine();
                printer.printNewMsg(newMsg);
            } catch (IOException e) {
                return;
            }
        }
    }

    public void stopMe(){
        this.working = false;
    }

}
