package org.example.udp;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client2 extends Thread {

    @Override
    public void run() {

        DatagramSocket socket = null;
        DatagramPacket packet = null;
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new DatagramSocket(7573);
            packet = new DatagramPacket(new byte[256], 256);

            while (true) {
                String message = scanner.nextLine();
                socket.send(new DatagramPacket(message.getBytes(),
                        message.getBytes().length, InetAddress.getByName("localhost"), 7878));

                socket.receive(packet);
                String receivedMessage = new String(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
                System.out.println("Received: " + receivedMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public static void main(String[] args) {
            Client2 client2 = new Client2();
        client2.start();
    }

}
