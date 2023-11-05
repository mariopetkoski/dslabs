package org.example.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class Client1 extends Thread {

    @Override
    public void run() {

    DatagramSocket socket = null;
    DatagramPacket packet = null;
    Scanner scanner = new Scanner(System.in);

        try {
            socket = new DatagramSocket(7878);
            packet = new DatagramPacket(new byte[256], 256);

            while (true) {
                socket.receive(packet);
                String receivedMessage = new String(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
                System.out.println("Received: " + receivedMessage);
                String message = scanner.nextLine();
                socket.send(new DatagramPacket(message.getBytes(),message.getBytes().length, InetAddress.getByName("localhost"), 7573));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client1 client1 = new Client1();
        client1.start();
    }
}
