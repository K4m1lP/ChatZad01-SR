package com.company.client.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Printer {

    private final Scanner scanner;
    private List<String> state = new ArrayList<>();
    private final int maxEnters = 15;
    private int freeIndex = 0;
    private boolean serverState = false;
    

    public Printer(Scanner scanner){
        this.scanner = scanner;
    }

    public void close(){
        this.scanner.close();
    }

    private void moveState(){
        List<String> r = new ArrayList<>();
        for(int i=0;i<maxEnters-1;i++)
            r.add(state.get(i+1));
        r.add("\n");
        r.add(state.get(maxEnters));
        r.add(state.get(maxEnters+1));
        this.state = r;
    }

    public synchronized void printNewMsg(String msg){
        System.out.println();
        if(freeIndex<maxEnters-1){
            state.set(freeIndex, msg+"\n");
            freeIndex++;
        } else {
            moveState();
            state.set(freeIndex, msg+"\n");
        }
        drawState();
    }

    public synchronized void print(String s){
        System.out.print(s);
    }

    public void setServer(boolean state){
        this.serverState = state;
        if(this.state.size() >= maxEnters){
            this.state.set(maxEnters, "==================================" + (serverState?" Connected":" Not connected") + "\n");
        }
    }

    public void startChat(){
        if(state.size()>0) {
            drawState();
            return;
        }

        for(int i=0;i<maxEnters;i++)
            state.add("\n");
        state.add("==================================" + (serverState?" Connected":" Not connected") + "\n");
        state.add("You: ");
        drawState();
    }

    public String getLine(){
        String r = this.scanner.nextLine();
        if(freeIndex<maxEnters-1){
            state.set(freeIndex, "You: "+r+"\n");
            freeIndex++;
        } else {
            moveState();
            state.set(freeIndex, "You: "+r+"\n");
        }
        drawState();
        return r;
    }
    
    private void drawState(){
        state.forEach(System.out::print);
    }
}
