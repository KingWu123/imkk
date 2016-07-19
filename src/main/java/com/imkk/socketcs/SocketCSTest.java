package com.imkk.socketcs;

import java.io.IOException;

/**
 * Created by kingwu on 7/18/16.
 */
public class SocketCSTest {

    public static void main(String[] args){

        System.out.println("main start");

        SocketServer socketServer = new SocketServer();
        socketServer.accept();


        System.out.println("main quit");
    }
}
