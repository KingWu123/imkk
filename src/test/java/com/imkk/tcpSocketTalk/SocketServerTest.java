package com.imkk.tcpSocketTalk;

/**
 * Created by kingwu on 7/22/16.
 */
public class SocketServerTest {

    public static void main(String[] args){

        System.out.println("main start");
        //服务端
        SocketServer socketServer = new SocketServer();
        socketServer.accept();
    }
}
