package com.company.client.ClientNetwork;

import com.company.client.ClientNetwork.Errors.Debug;
import com.company.client.ClientNetwork.States.StartState;
import com.company.client.ClientNetwork.States.State;


public class Client {

    private State currentState;

    public Client(String ip){
        if(Debug.debug)
            System.out.println("Client constructor, setting state");
        this.currentState = new StartState(ip, this);
    }

    public void start(){
        if(Debug.debug)
            System.out.println("Client start, start client while not close");
        boolean clientWorking = true;
        while (clientWorking){
            clientWorking = currentState.proceed();
        }
    }

    public void changeState(State state){
        if(Debug.debug)
            System.out.println("Client changeState " + currentState + " -> " + state);
        this.currentState = state;
        this.currentState.setContext(this);
    }
}
