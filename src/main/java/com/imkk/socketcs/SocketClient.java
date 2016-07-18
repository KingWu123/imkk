package com.imkk.multicast.socketcs;

import java.io.*;
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

    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 40005;
    private static final int TIME_OUT = 10 * 1000;//10S

    private Socket mClientSocket;
    BufferedWriter mOutputStream;
    BufferedReader mInputStream;

    public SocketClient(){

        try {
            //1.创建套接字
            mClientSocket = new Socket(SERVER_IP, PORT);
            mClientSocket.setSoTimeout(TIME_OUT);

            //2.通过套接字的I/O流与服务端通信
            mOutputStream = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
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
        });

        new Thread(new Runnable() {
            public void run() {
                receiveMessage();
            }
        });
    }


    private void sendMessage(){

        boolean flag = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("请输入信息");
        while (flag){

            try {
                String str = reader.readLine();
                mOutputStream.write(str);

                System.out.println("client: " + str);
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
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveMessage(){

        try {
            String receiveMsg = mInputStream.readLine();
            System.out.println("server: " + receiveMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
