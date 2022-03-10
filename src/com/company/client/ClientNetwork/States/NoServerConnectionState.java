package com.company.client.ClientNetwork.States;

import com.company.client.ClientNetwork.Client;
import com.company.client.ClientNetwork.Errors.Debug;
import com.company.client.ClientNetwork.Errors.NO_RECEIVE_ERROR;
import com.company.client.ClientNetwork.Errors.NO_SERVER_CONNECTION;
import com.company.client.ClientNetwork.Network.NoServerNetwork;
import com.company.client.ClientNetwork.Network.ServerNetwork;
import com.company.client.Printer.Printer;

import java.io.IOException;
import java.util.Scanner;

public class NoServerConnectionState implements State {

    private Client context;
    private final NoServerNetwork network;
    private final Printer printer;
    private String ip;
    private String nick;

    public NoServerConnectionState(String ip, NoServerNetwork network, Printer printer){
        if(Debug.debug)
            System.out.println("No server network state constructor");
        this.printer = printer;
        this.network = network;
        this.ip = ip;

        Scanner scan = new Scanner(System.in);
        printer.print("Your Nick: ");
        nick = scan.nextLine();

        this.network.setNick(nick);
        this.printer.startChat();
    }

    @Override
    public boolean proceed() {
        String specialMulti = "-M";
        String userInput = printer.getLine();
        if(userInput.equals("EXIT"))
            return false;
        if(userInput.contains(specialMulti)) {
            try {
                network.sendMulticast(userInput);
            } catch (IOException ignored) {}
        }
        try {
            ServerNetwork serverNetwork = new ServerNetwork(ip, printer);
            printer.setServer(true);
            context.changeState(new ServerConnectionState(network, serverNetwork, printer, nick));
        } catch (NO_SERVER_CONNECTION | NO_RECEIVE_ERROR ignored) {}

        return true;
    }

    @Override
    public void setContext(Client context) {
        this.context = context;
    }
}
