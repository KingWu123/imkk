package com.imkk.socketcs;

import java.io.IOException;

/**
 * Created by kingwu on 7/18/16.
 */
public class SocketCSTest {

    public static void main(String[] args){

        System.out.println("main start");

        SocketClient socketClient = new SocketClient();
        socketClient.communicate();


       while (true){
           try {
               Thread.sleep(100000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
    }
}
