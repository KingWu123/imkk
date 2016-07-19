package com.imkk.socketcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by kingwu on 7/18/16.
 *
 * 实现 socket通信的客户端
 *
 *
 *  1. 创建一个Socket实例：构造函数向指定的远程主机和端口建立一个TCP连接；
 *  2. 通过套接字的I/O流与服务端通信；
 *  3. 使用Socket类的close方法关闭连接。
 */
public class SocketClient {

    private static final String SERVER_IP = "10.240.252.97";
    private static final int PORT = 65534;
    private static final int TIME_OUT = 10 * 1000;//10S

    private Socket mClientSocket;
    PrintWriter mOutputStream;
    BufferedReader mInputStream;

    public SocketClient(){

        try {
            //1.创建套接字
            mClientSocket = new Socket(SERVER_IP, PORT);
           //1 mClientSocket.setSoTimeout(TIME_OUT);

            //2.通过套接字的I/O流与服务端通信
            mOutputStream = new PrintWriter(mClientSocket.getOutputStream(), true);
            mInputStream = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //建立聊天通信
    public void communicate(){


        new Thread(new Runnable() {
            public void run() {
                sendMessage();
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                receiveMessage();
            }
        }).start();
    }


    private void sendMessage(){

        boolean flag = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("请输入信息");
        while (flag){

            try {
                String str = reader.readLine();
                mOutputStream.println(str);

                if (str.equals("bye")){
                    flag = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //关闭键盘输入
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //关闭套接字
        if (mClientSocket != null){
            try {
                mInputStream.close();
                mOutputStream.close();
                mClientSocket.close();

                mInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("聊天关闭");
    }

    private void receiveMessage(){

        try {
            while (true) {
                String receiveMsg = mInputStream.readLine();
                System.out.println("server: " + receiveMsg);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }
}
