package com.imkk.socketcs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kingwu on 7/18/16.
 *
 *
 * socket 通信服务器端的实现
 *
 * 1、创建一个ServerSocket实例并指定本地端口，用来监听客户端在该端口发送的TCP连接请求；
 *
 * 2、重复执行：
 *     1）调用ServerSocket的accept（）方法以获取客户端连接，并通过其返回值创建一个Socket实例；
 *     2）为返回的Socket实例开启新的线程，并使用返回的Socket实例的I/O流与客户端通信；
 *     3）通信完成后，使用Socket类的close（）方法关闭该客户端的套接字连接。
 */
public class SocketServer {

    private ServerSocket mServerSocket;
    private ExecutorService mExec = Executors.newCachedThreadPool();

    public SocketServer(){

        try {
            mServerSocket = new ServerSocket(40005);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept(){

        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    try {
                        Socket clientSocket = mServerSocket.accept();

                        mExec.execute(new SocketRunnable(clientSocket));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // TODO: 7/18/16  怎么退出
            }
        });

    }

    //socket server怎么退出
    public void close(){

        mExec.shutdown();

        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private static class SocketRunnable implements Runnable{

        private Socket mClient = null;

        public SocketRunnable(Socket client){
            mClient = client;
        }

        public void run() {

            //获取Socket的输入流，用来接收从客户端发送过来的数据
            try {
                BufferedReader readerBuf = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
                BufferedWriter writerBuf = new BufferedWriter(new OutputStreamWriter(mClient.getOutputStream()));

                boolean flag = true;

                System.out.println("one client come");

                while (flag){
                    String str = readerBuf.readLine();
                    if (str.equals("bye")){
                        flag = false;
                        System.out.println("good bye");
                    }else {
                        System.out.println("client " + mClient.getInetAddress() + " : " + str);
                    }

                    writerBuf.write("echo " + str);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
