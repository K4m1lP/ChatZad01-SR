package com.company.server;

import com.company.server.Handlers.TCPUserThread;
import com.company.server.Handlers.UDPListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void start(){
        System.out.println("[MAIN][JAVA SERVER]");
        int portNumber = 12345;
        BroadcastManager broadcastManager = new BroadcastManager();
        UDPListener udpListener = new UDPListener(portNumber, broadcastManager);
        udpListener.start();
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[MAIN] new client just joined");
                TCPUserThread newUser = new TCPUserThread(clientSocket, broadcastManager);
                newUser.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
