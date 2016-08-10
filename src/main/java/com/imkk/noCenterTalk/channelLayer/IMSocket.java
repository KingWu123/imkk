package com.imkk.noCenterTalk.channelLayer;

import com.imkk.noCenterTalk.domain.Message;
import com.imkk.udpSocketTalk.UdpMessage;
import com.imkk.udpSocketTalk.UserData;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kingwu on 8/9/16.
 *
 * 提供 无中心 聊天系统的 socket通信功能
 *
 */
public class IMSocket {

    private static final String BROADCAST_IP = "230.0.0.1";//广播IP
    private static final int BROADCAST_INT_PORT = 45005;   //广播port


    private MulticastSocket mBroadcastSocket;//广播
    private InetAddress mBroadCastAddress;

    private ServerSocket mServerSocket;

    private ConcurrentHashMap<String, Socket> mFriendsSocketMap;

    private ExecutorService mExec = Executors.newCachedThreadPool();

    public IMSocket(){

        try {
            mBroadcastSocket = new MulticastSocket(BROADCAST_INT_PORT);
            mBroadCastAddress = InetAddress.getByName(BROADCAST_IP);

            mServerSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void jionGroup(String userId) throws IOException {

        //加入广播组
        mBroadcastSocket.joinGroup(InetAddress.getByName(BROADCAST_IP));

        //启动服务器,接受朋友发送的消息
        startServer();

        //广播自身消息,告知其他用户自身的serverSocket地址
        broadcastSelf(userId);

        //接受其他用户广播,并与之建立tcp连接
        receiveBroadcast();
    }

    private void leaveGroup(){

    }

    private void startServer(){
        mExec.execute(new Runnable() {
            public void run() {

                try {
                    while (true) {
                        Socket friendsSocket = mServerSocket.accept();


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean broadcastSelf(String id) throws IOException {

        BroadcastSocketMessage message = new BroadcastSocketMessage(id, mServerSocket.getInetAddress().getHostName(), mServerSocket.getLocalPort());

        DatagramSocket sender = new DatagramSocket();

        byte[] bytes = message.toBytes();
        if (bytes.length > 500){
            return false;
        }

        DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, mBroadCastAddress, BROADCAST_INT_PORT);
        sender.send(packet);

        return true;
    }

    private boolean receiveBroadcast(){

        mExec.execute(new Runnable() {
            public void run() {

                while (true){
                    byte[] buffer = new byte[500];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);

                    try {
                        mBroadcastSocket.receive(receivePacket);
                        byte[] body = receivePacket.getData();
                        BroadcastSocketMessage broadcastSocketMessage = new BroadcastSocketMessage();
                        broadcastSocketMessage.toMessage(body);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        return true;
    }



    private static class BroadcastSocketMessage implements Message{


        private String mId;
        private String mIP;
        private int mPort;

        public BroadcastSocketMessage(){

        }

        public BroadcastSocketMessage(String id, String ip, int port){
            mId = id;
            mIP = ip;
            mPort = port;
        }

        public byte[] toBytes() {
            return new byte[0];
        }

        public void toMessage(byte[] bytes) {

        }
    }
}
