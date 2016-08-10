package com.imkk.centerTalk.server.soketChannel;

import java.net.DatagramPacket;

/**
 * Created by kingwu on 8/10/16.
 */
public class UdpSeverSocket implements Runnable {

    private DatagramPacket mReceivePacket;

    public UdpSeverSocket(DatagramPacket receivePacket){
        mReceivePacket = receivePacket;
    }


    public void run() {

    }
}
