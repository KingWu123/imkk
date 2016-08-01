package com.imkk.noCenterTalk;

import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.net.*;

/**
 * Created by kingwu on 8/1/16.
 *
 * 组广播结构,用于处理
 */
public class GroupBroadcast {


    private static final String BROADCAST_IP = "230.0.0.1";//广播IP
    private static final int BROADCAST_INT_PORT = 45005;   //广播port

    private InetAddress mBroadCastAddress;
    private MulticastSocket mBroadcastSocket;


    private static final int MAX_USER_DATA_PACKET_SIZE = 1024;

    public GroupBroadcast(){

        try {
            mBroadcastSocket = new MulticastSocket(BROADCAST_INT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入到广播组,用于 接收用户 广播信息
     */
    public void jionGroup(){

        try {

            mBroadCastAddress = InetAddress.getByName(BROADCAST_IP);
            mBroadcastSocket.joinGroup(mBroadCastAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 发送用户 进入组播后的输入, 如上线、离线、隐身等
     * @param sender DatagramSocket
     * @param userData 用户进入组播是 传送的数据
     * @return
     */
    public Boolean sendUserData(DatagramSocket sender, UserData userData){


        byte[] bytes = userData.getBytes();
        if (bytes.length > MAX_USER_DATA_PACKET_SIZE){
            return false;
        }


        DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, mBroadCastAddress, BROADCAST_INT_PORT);

        try {
            sender.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public UserData receiveUserData(){

        byte[] buffer = new byte[MAX_USER_DATA_PACKET_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);

        try {
            mBroadcastSocket.receive(receivePacket);

            byte[] body = receivePacket.getData();
            UserData userData = UserData.userData(body);

            return userData;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



}
