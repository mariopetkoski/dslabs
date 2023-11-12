package org.example.binarySerialization;

import org.example.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class Receiver extends Thread {
    DatagramSocket socket;
    Message message;

    public Receiver(DatagramSocket socket, Message message) {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void run() {
        DatagramPacket packet = null;

        try {
            packet = new DatagramPacket(new byte[256], 256);
            ByteArrayInputStream byteArrayInputStream = null;
            ObjectOutputStream objectOutputStream = null;


                byte[] recievedData = new byte[256];
                socket.receive(packet);
                byteArrayInputStream = new ByteArrayInputStream(recievedData);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                message = (Message) objectInputStream.readObject();



                System.out.println(message.toString());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

class Sender extends Thread {
    DatagramSocket socket;

    Message message;

    public Sender(DatagramSocket socket, Message message) {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void run() {

        DatagramPacket packet = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;

        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();

            byte[] messageBytes = byteArrayOutputStream.toByteArray();
            socket.send(new DatagramPacket(messageBytes,
                    messageBytes.length, InetAddress.getByName("localhost"), 8484));
            System.out.println("Object sent: ");
            System.out.println(message.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class ClientUDP extends Thread {

    int receivePort;
    int sendPort;
    Message message;

    Receiver receiver;

    Sender sender;
    DatagramSocket socket;

    public ClientUDP(int receivePort, int sendPort, Message message) throws SocketException {
        this.receivePort = receivePort;
        this.sendPort = sendPort;
        this.socket = new DatagramSocket(receivePort);
        this.message = message;
    }

    @Override
    public void run() {
        Receiver receiver = new Receiver(socket, message);
        Sender sender = new Sender(socket, message);
        receiver.start();
        sender.start();
    }

    public static void main(String[] args) throws SocketException {
        ArrayList<String> arrayList = new ArrayList<>();
        Message message = new Message("Mario", "Tomi", "this is the message text", arrayList);

        ClientUDP clientUdp = new ClientUDP(6767, 8484, message);
        clientUdp.start();
    }
}
