package org.example.tcp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class Receiver extends Thread {
    private Socket socket;
    private int timeoutCounter = 0;

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
                            break;
                        } else {
                            timeoutCounter++;
                            Thread.sleep(1000);
                        }
                    }
                }
            } else {
                System.out.println("Invalid connection request. Closing the connection.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class Sender extends Thread {
    private Socket socket;

    public Sender(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

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
    private int sendPort;
    private int receivePort;

    private ServerSocket serverSocket;

    public ClientTCP(int sendPort, int receivePort) throws IOException {
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        serverSocket = new ServerSocket(receivePort);
    }

    public void start() {
        try {
            Socket socket = new Socket("localhost", sendPort);
            Sender sender = new Sender(socket);
            sender.start();

            while (true) {
                Socket receiverSocket = serverSocket.accept();
                Receiver receiver = new Receiver(receiverSocket);
                receiver.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ClientTCP clientTcp = new ClientTCP(1234, 5678);
        clientTcp.start();
    }
}
