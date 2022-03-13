package com.company.client.ClientNetwork.States;

import com.company.client.ClientNetwork.Client;
import com.company.client.ClientNetwork.Network.NoServerNetwork;
import com.company.client.ClientNetwork.Network.ServerNetwork;
import com.company.client.Printer.Printer;

public class EndState implements State{

    private Client context;
    private final NoServerNetwork noServerNetwork;
    private final Printer printer;
    private final ServerNetwork serverNetwork;

    public EndState(NoServerNetwork noServerNetwork, Printer printer, ServerNetwork serverNetwork){
        this.noServerNetwork = noServerNetwork;
        this.printer = printer;
        this.serverNetwork = serverNetwork;
    }

    @Override
    public boolean proceed() {
        if(noServerNetwork!=null)
            noServerNetwork.close();
        if(serverNetwork!=null)
            serverNetwork.close();
        if(printer!=null)
            printer.close();
        return false;
    }

    @Override
    public void setContext(Client context) {
        this.context = context;
    }
}
