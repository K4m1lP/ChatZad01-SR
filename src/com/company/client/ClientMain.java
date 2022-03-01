package com.company.client;

import com.company.client.ClientNetwork.Client;

import java.io.IOException;


public class ClientMain {

    public static void main(String[] args) throws IOException {

        new Client().start(args);

    }
}


