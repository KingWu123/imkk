package com.imkk.noCenterTalk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by kingwu on 7/27/16.
 */
public class UDPSocketTest {

    public static void main(String[] args){

        final UdpSocketUser udpSocketUser1 = new UdpSocketUser();
        udpSocketUser1.joinGroup();

        new Thread(new Runnable() {
            public void run() {
                sendMessage(udpSocketUser1);
            }
        }).start();


        new Thread(new Runnable() {
            public void run() {
                receiveMessage(udpSocketUser1);
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

    private static void sendMessage(UdpSocketUser udpSocketUser){

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                //输入的时候,输入用户id 和 发送的话,
                String str = reader.readLine();
                if (str == null || str.equals("bye")){
                    break;
                }


                String[] result = str.split(" ");
                if (result.length == 2) {
                    UserData friend = udpSocketUser.getFriendById(result[0]);
                    if (friend != null) {
                        UdpMessage udpMessage  = new UdpMessage(UdpMessage.NORMAL_MESSAGE, result[1].getBytes());
                        udpSocketUser.sendMessage(friend.getUserIP(), friend.getUserPort(), udpMessage);
                    }
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        udpSocketUser.close();

    }

    private static void receiveMessage(UdpSocketUser udpSocketUser){

        boolean flag = true;

        while (flag) {
            try {
                UdpMessage udpMessage = udpSocketUser.receiveMessage();

                if (udpMessage.getType() == UdpMessage.NORMAL_MESSAGE) {
                    System.out.println("\"" + new String(udpMessage.getBody()) + "\"");
                }

            } catch (IOException e) {
                e.printStackTrace();
                flag = false;
            }catch (Exception e){

                flag = false;
            }
        }


    }

}
