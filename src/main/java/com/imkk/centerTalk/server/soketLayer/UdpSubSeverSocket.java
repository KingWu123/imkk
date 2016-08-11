package com.imkk.centerTalk.server.soketLayer;

import java.net.DatagramPacket;

/**
 * Created by kingwu on 8/10/16.
 */
public class UdpSubSeverSocket implements Runnable {

    private DatagramPacket mReceivePacket;

    public UdpSubSeverSocket(DatagramPacket receivePacket){
        mReceivePacket = receivePacket;
    }


    public void run() {

    }
}
