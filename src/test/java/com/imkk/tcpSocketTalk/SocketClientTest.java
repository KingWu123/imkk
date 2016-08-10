package com.imkk.tcpSocketTalk;

/**
 * Created by kingwu on 7/18/16.
 */
public class SocketClientTest {

    public static void main(String[] args){

        //客户端
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
