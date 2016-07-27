package com.imkk.noCenterTalk;

/**
 * Created by kingwu on 7/18/16.
 */

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *  写一个多人聊天服务，不带中心， 熟悉互相探测并发起聊天过程
 *
 *  利用 udp的广播 ,探知端与端的信息
 */
public class MulticastSocketIMClient {

    private static final String BROADCAST_IP = "230.0.0.1";//广播ip
    private static final int BROADCAST_INT_PORT = 40005;//端口号


    private MulticastSocket mBroadSocket; //用于接受广播消息
    private InetAddress mBroadAddress;    //广播地址
    private DatagramSocket mSender; //


    public MulticastSocketIMClient(){

        try {
            mBroadSocket = new MulticastSocket(BROADCAST_INT_PORT);
            mBroadAddress = InetAddress.getByName(BROADCAST_IP);
            mSender = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void join(){
        try {
            mBroadSocket.joinGroup(mBroadAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    //
    private static class PacketReceiverListener implements  Runnable{

        public void run() {

        }
    }

}


