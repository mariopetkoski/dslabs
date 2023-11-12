package org.example.jsonSerialization;

import org.example.Message;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;


class Receiver extends Thread {
    private Socket socket;
    private int timeoutCounter = 0;

    private Message message;

    private static final Gson gson = new Gson();

    public Receiver(Socket socket, Message message) {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void run() {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String jsonMessage = reader.readLine();

            Message message = gson.fromJson(jsonMessage, Message.class);
            System.out.println(message.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

class Sender extends Thread {
    private Socket socket;

    private Message message;

    private static final Gson gson = new Gson();

    public Sender(Socket socket, Message message) {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void run() {
       PrintWriter writer = null;

       try {
           writer = new PrintWriter(socket.getOutputStream(), true);
           String jsonMessage = gson.toJson(message);
           writer.println(jsonMessage);

       } catch (IOException e) {
           throw new RuntimeException(e);
       }

    }
    }

public class ClientTCP {
    private int sendPort;
    private int receivePort;

    private ServerSocket serverSocket;

    private Message message;

    public ClientTCP(int sendPort, int receivePort, Message message) throws IOException {
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        serverSocket = new ServerSocket(receivePort);
        this.message = message;
    }

    public void start() {
        try {
            Socket socket = new Socket("localhost", sendPort);
            Sender sender = new Sender(socket, message);
            sender.start();

            while (true) {
                Socket receiverSocket = serverSocket.accept();
                Receiver receiver = new Receiver(receiverSocket, message);
                receiver.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();
        Message message = new Message("Mario", "Tomi", "this is the message text", arrayList);

        ClientTCP clientTcp = new ClientTCP(1234, 5678, message);
        clientTcp.start();
    }
}
