package com.imkk.noCenterTalk;

import java.io.IOException;
import java.net.*;

/**
 * Created by kingwu on 8/1/16.
 *
 * 组广播对象,用于处理: 加入广播组,  广播用户数据, 接受用户广播数据等
 */
public class BroadcastService {


    private static final String BROADCAST_IP = "230.0.0.1";//广播IP
    private static final int BROADCAST_INT_PORT = 45005;   //广播port

    private InetAddress mBroadCastAddress;
    private MulticastSocket mBroadcastSocket;


    private static final int MAX_PACKET_SIZE = 1024;

    public BroadcastService(){

        try {
            mBroadcastSocket = new MulticastSocket(BROADCAST_INT_PORT);
            mBroadCastAddress = InetAddress.getByName(BROADCAST_IP);

            mBroadcastSocket.setLoopbackMode(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入到广播组,用于 接收用户 广播信息
     */
    public void joinGroup() throws IOException {
        mBroadcastSocket.joinGroup(mBroadCastAddress);

    }

    /**
     * 离开广播组
     * @throws IOException
     */
    public void leaveGroup() throws IOException {
        mBroadcastSocket.leaveGroup(mBroadCastAddress);
    }


    /**
     * 关闭socket
     */
    public void close(){
        mBroadcastSocket.close();
    }

    /**
     * 发送用户 进入组播后的输入, 如上线、离线、隐身等
     * @param sender DatagramSocket
     * @param userData 用户进入组播是 传送的数据
     * @return
     */
    public boolean sendUserBroadcastData(DatagramSocket sender, UserData userData) throws IOException{

        byte[] bytes = userData.toBytes();
        if (bytes.length > MAX_PACKET_SIZE){
            return false;
        }

        DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, mBroadCastAddress, BROADCAST_INT_PORT);
        sender.send(packet);

        return true;
    }

    public UserData receiveUserBroadcastData() throws IOException{

        byte[] buffer = new byte[MAX_PACKET_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);

        mBroadcastSocket.receive(receivePacket);

        byte[] body = receivePacket.getData();
        UserData userData = UserData.userData(body, receivePacket.getLength());

        //不管用户自己填的是什么ip/port,这里都以packet包里面的ip/port为准
        InetAddress remoteAddress =  receivePacket.getAddress();
        int remotePort = receivePacket.getPort();
        userData.setUserIP(remoteAddress.getHostAddress());
        userData.setUserPort(remotePort);

        return userData;
    }



}
