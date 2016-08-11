package com.imkk.centerTalk.server.soketLayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kingwu on 8/10/16.
 *
 * 实现一个有中心的聊天系统, 这个是sever端的入口, 可以接受TCP和UDP的请求
 *
 * 当有客户链接后, 如果是TCP请求, 则生成的socket都传给{@see TcpSubServerSocket}去处理
 *               如果是UDP请求, 则收到的数据包都传给{@see UdpSubSeverSocket}去处理
 *
 */
public class IMServer {

    private final int TCP_PORT = 65534;
    private final int UDP_PORT = 65535;

    private ServerSocket mTcpServerSocket;
    private DatagramSocket mUdpServerSocket;

    private ExecutorService mExec = Executors.newCachedThreadPool();

    public IMServer(){

        try {
            mTcpServerSocket = new ServerSocket(TCP_PORT);
            mUdpServerSocket = new DatagramSocket(UDP_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void tcpAccept(){

        while (true) {
            try {
                Socket clientSocket = mTcpServerSocket.accept();
                mExec.execute(new TcpSubServerSocket(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void udpReceive(){

        while (true){
            try {

                byte[] buffer = new byte[1024 * 16];
                DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);
                mUdpServerSocket.receive(receivePacket);

                mExec.execute(new UdpSubSeverSocket(receivePacket));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
