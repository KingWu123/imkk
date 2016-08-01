package com.imkk.noCenterTalk;

import java.io.IOException;
import java.net.*;

/**
 * Created by kingwu on 7/27/16.
 *
 * Udp 通信的 一个用户端。  随机bind一个本地的有效localIP 和 port
 *
 */
public class UDPSocketUser {

    DatagramSocket mUserSocket;


    public UDPSocketUser() {

        try {
            mUserSocket = new DatagramSocket();
            mUserSocket.bind(new InetSocketAddress(0));

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }



    public UDPSocketUser(int port) {

        try {
            mUserSocket = new DatagramSocket(port);

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     *  发送一条消息
     * @param hostName 远端的 地址
     * @param port     远端应用端口
     * @param sendBytes 发送的内容
     * @return 发送数据是否成功
     */
    public boolean sendMessage(String host, int port, byte[]sendBytes) {

        try {

            InetAddress address = InetAddress.getByName(host);
            DatagramPacket sendPacket
                    = new DatagramPacket(sendBytes , 0 ,sendBytes.length , address , port);
            mUserSocket.send(sendPacket);
            return true;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public DatagramPacket receiveMessage() throws IOException{

        byte[] buffer = new byte[1024 * 16];
        DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);

        mUserSocket.receive(receivePacket);

        //receivePacket.getAddress();
        //receivePacket.getPort();

        return receivePacket;
    }


    @Override
    public String toString() {
        return "ip: " + mUserSocket.getLocalAddress() + " port: " + mUserSocket.getLocalPort();
    }
}
