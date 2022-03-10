package com.company.client.ClientNetwork.States;

import com.company.client.ClientNetwork.Client;
import com.company.client.ClientNetwork.Errors.NO_RECEIVE_ERROR;
import com.company.client.ClientNetwork.Network.NoServerNetwork;
import com.company.client.ClientNetwork.Network.ServerNetwork;
import com.company.client.Printer.Printer;

import java.io.IOException;
import java.util.Scanner;

public class ServerConnectionState implements State {

    private Client context;

    private NoServerNetwork noServerNetwork;
    private ServerNetwork serverNetwork;
    private Printer printer;
    private String nick;

    public ServerConnectionState(NoServerNetwork noServerNetwork, ServerNetwork serverNetwork, Printer printer) throws NO_RECEIVE_ERROR {
        this(noServerNetwork, serverNetwork, printer, null);
    }

    public ServerConnectionState(NoServerNetwork noServerNetwork, ServerNetwork serverNetwork, Printer printer, String nick) throws NO_RECEIVE_ERROR {

        this.noServerNetwork = noServerNetwork;
        this.serverNetwork = serverNetwork;
        this.printer = printer;

        Scanner scan = new Scanner(System.in);

        if(nick == null){
            printer.print("Your Nick: ");
            String nickStr = scan.nextLine();
            serverNetwork.sendTCP(nickStr);
            while (!serverNetwork.receiveTCP().equals("ACK")){
                printer.print("This nick is already in use:");
                nickStr = scan.nextLine();
                serverNetwork.sendTCP(nickStr);
            }
            this.nick = nickStr;
        } else {
            serverNetwork.sendTCP(nick);
            while (!serverNetwork.receiveTCP().equals("ACK")){
                printer.print("This nick is already in use:");
                nick = scan.nextLine();
                serverNetwork.sendTCP(nick);
            }
            this.nick = nick;
        }

        serverNetwork.startListening();
        serverNetwork.setNick(this.nick);
        noServerNetwork.setNick(this.nick);

        printer.startChat();
    }

    @Override
    public boolean proceed() {

        String specialMulti = "-M";
        String specialUdp = "-U";

        String userInput = printer.getLine();
        if(userInput.equals("EXIT"))
            return false;
        if(userInput.contains(specialMulti)) {
            try {
                noServerNetwork.sendMulticast(userInput);
                return true;
            } catch (IOException ignored) {}
        }
        if(userInput.contains(specialUdp)){
            try {
                serverNetwork.sendUDP(userInput);
                return true;
            } catch (IOException ignored) {
                // TODO:
                System.out.println("Server shot down");
                serverNetwork.close();
            }
        }
        serverNetwork.sendTCP(userInput);
        return true;
    }

    @Override
    public void setContext(Client context) {
        this.context = context;
    }
}
