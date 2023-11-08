package org.example.tcp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class Receiver extends Thread {
    Socket socket;
    int timeoutCounter = 0;

    public Receiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String firstMessage = reader.readLine();

            if (firstMessage != null && firstMessage.startsWith("connect:")) {
                System.out.println("Connected: " + firstMessage);

                while (true) {
                    String receivedMessage = reader.readLine();
                    if (receivedMessage != null) {
                        System.out.println("Received: " + receivedMessage);
                        timeoutCounter = 0;
                    } else {
                        if (timeoutCounter >= 60) {
                            System.out.println("Connection closed due to inactivity.");
                            socket.close();
                            break;
                        } else {
                            timeoutCounter++;
                            Thread.sleep(1000);
                        }
                    }
                }
            } else {
                System.out.println("Invalid connection request. Closing the connection.");
                socket.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Sender extends Thread {
    Socket socket;

    public Sender(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String message = scanner.nextLine();
                writer.println(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class ClientTCP {
    int port;

    Receiver receiver;
    Sender sender;
    Socket socket;

    public ClientTCP(int port) {
        this.port = port;
    }

    public void start() {
        try {
            socket = new Socket("localhost", port);
            Receiver receiver = new Receiver(socket);
            Sender sender = new Sender(socket);
            receiver.start();
            sender.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientTCP clientTcp = new ClientTCP(8484);
        clientTcp.start();
    }
}
