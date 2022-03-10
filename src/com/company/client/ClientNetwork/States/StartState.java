package com.company.client.ClientNetwork.States;

import com.company.client.ClientNetwork.Client;
import com.company.client.ClientNetwork.Errors.NO_RECEIVE_ERROR;
import com.company.client.ClientNetwork.Errors.NO_SERVER_CONNECTION;
import com.company.client.ClientNetwork.Network.ServerNetwork;
import com.company.client.ClientNetwork.Network.NoServerNetwork;
import com.company.client.Printer.Printer;

import java.util.Scanner;

public class StartState implements State {

    private Client context;
    private final String ip;

    public StartState(String ip, Client context){
        this.context = context;
        this.ip = ip;
        System.out.println("Welcome, this is the best chat you have ever been experience");
        System.out.println("Some info:");
        System.out.println("-  type EXIT to finish chatting");
        System.out.println("-  type -U at beginning of message to send it using UDP protocol (broadcast)");
        System.out.println("-  type -M at beginning of message to send it using UDP protocol (multicast)");
    }

    @Override
    public boolean proceed() {
        Scanner scan = new Scanner(System.in);
        Printer printer = new Printer(scan);
        NoServerNetwork noServerNetwork = new NoServerNetwork(printer);
        try{
            ServerNetwork network = new ServerNetwork(ip, printer);
            printer.setServer(true);
            context.changeState(new ServerConnectionState(noServerNetwork, network, printer));
        } catch (NO_SERVER_CONNECTION e){
            printer.setServer(false);
            context.changeState(new NoServerConnectionState(ip, noServerNetwork, printer));
        } catch (NO_RECEIVE_ERROR e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void setContext(Client context) {
        this.context = context;
    }
}
