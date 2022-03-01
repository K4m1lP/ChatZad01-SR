package com.company.client.ClientNetwork;

import com.company.client.Printer.Printer;

import java.util.Scanner;

public class Client {

    public void start(String[] args){

        System.out.println("Welcome, this is the best chat you have ever been experience");
        System.out.println("Some info:");
        System.out.println("-  type EXIT to finish chatting");
        System.out.println("-  type -U at beginning of message to send it using UDP protocol (broadcast)");
        System.out.println("-  type -M at beginning of message to send it using UDP protocol (multicast)");

        Scanner scan = new Scanner(System.in);
        Printer printer = new Printer(scan);
        Network network = null;
        String nick;
        String specialU = "-U";
        String specialMulti = "-M";

        try {
            network = new Network(args[0], printer);
            printer.print("Your Nick: ");
            String nickStr = scan.nextLine();
            network.sendTCP(nickStr);
            while (!network.receiveTCP().equals("ACK")){
                printer.print("This nick is already in use:");
                nickStr = scan.nextLine();
                network.sendTCP(nickStr);
            }
            nick = nickStr;
            network.setNick(nick);

            printer.startChat();
            network.startListen();
            String userInput = "";
            while (!userInput.equals("EXIT")){
                userInput = printer.getLine();
                if(userInput.contains(specialU))
                    network.sendUDP(userInput);
                else if(userInput.contains(specialMulti))
                    network.sendMulticast(userInput);
                else
                    network.sendTCP(userInput);
            }
        } catch (Exception e) {
            printer.close();
            System.out.println("[Application failed] Probably no server running");
        } finally {
            if (network != null)
                network.close();
            printer.close();
        }
    }
}
