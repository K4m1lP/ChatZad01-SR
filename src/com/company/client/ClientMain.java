package com.company.client;

import com.company.client.ClientNetwork.Client;
import com.company.client.ClientNetwork.Errors.Debug;

import java.io.IOException;


public class ClientMain {

    public static void main(String[] args) throws IOException {
        if(Debug.debug)
            System.out.println("Client starting");
        new Client(args[0]).start();
    }
}


