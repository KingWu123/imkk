package com.imkk.noCenterTalk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by kingwu on 7/27/16.
 *
 *
 */
public class UDPSocketTest2 {

    public static void main(String[] args){

        final UDPSocketUser udpSocketUser2 = new UDPSocketUser(50001);
        udpSocketUser2.joinGroup();


        new Thread(new Runnable() {
            public void run() {
                sendMessage(udpSocketUser2);
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                receiveMessage(udpSocketUser2);
            }
        }).start();


        while (true){
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendMessage(UDPSocketUser udpSocketUser){

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            boolean flag = true;

            while (flag) {

                String str = reader.readLine();
                if (str.equals("bye")){
                    flag = false;
                }

                System.out.println("I " + udpSocketUser );
                System.out.println("         \"" + str + "\"");
                udpSocketUser.sendMessage("localhost", 60001, str.getBytes());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void receiveMessage(UDPSocketUser udpSocketUser){

        boolean flag = true;

        try {

            while (flag){

                DatagramPacket datagramPacket = udpSocketUser.receiveMessage();

                byte[] body = datagramPacket.getData();
                int length = datagramPacket.getLength();
                String msg = new String(body, 0, length);
                InetAddress remoteAddress = datagramPacket.getAddress();
                int port = datagramPacket.getPort();

                System.out.println("friends:  " +  remoteAddress + " port:"  + port);
                System.out.println("  \"" + msg + "\"");

                if (msg.equals("bye")){
                    flag = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
