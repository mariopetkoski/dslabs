package org.example.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;

class Receiver extends Thread {
    DatagramSocket socket;

    public Receiver(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(new byte[256], 256);

            while (true) {
                socket.receive(packet);
                String receivedMessage = new String(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
                System.out.println("Received: " + receivedMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Sender extends Thread {
    DatagramSocket socket;

    public Sender(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        DatagramPacket packet = null;
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                String message = scanner.nextLine();
                socket.send(new DatagramPacket(message.getBytes(),
                        message.getBytes().length, InetAddress.getByName("localhost"), 8484));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class ClientUDP extends Thread {

    int receivePort;
    int sendPort;

    Receiver receiver;

    Sender sender;
    DatagramSocket socket;

    public ClientUDP(int receivePort, int sendPort) throws SocketException {
        this.receivePort = receivePort;
        this.sendPort = sendPort;
        this.socket = new DatagramSocket(receivePort);
    }

    @Override
    public void run() {
        Receiver receiver = new Receiver(socket);
        Sender sender = new Sender(socket);
        receiver.start();
        sender.start();
    }

    public static void main(String[] args) throws SocketException {
        ClientUDP clientUdp = new ClientUDP(6767, 8484);
        clientUdp.start();
    }
}
