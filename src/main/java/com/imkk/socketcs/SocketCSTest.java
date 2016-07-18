package com.imkk.socketcs;

/**
 * Created by kingwu on 7/18/16.
 */
public class SocketCSTest {

    public static void main(String[] args){

        System.out.print("main start");
        SocketServer socketServer = new SocketServer();
        socketServer.accept();

      //  SocketClient socketClient = new SocketClient();
      //  socketClient.communicate();

        System.out.print("main quit");


    }
}
