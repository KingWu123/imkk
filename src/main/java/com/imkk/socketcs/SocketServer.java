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
            mServerSocket = new ServerSocket(65534);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept(){

        while (true) {
            try {
                Socket clientSocket = mServerSocket.accept();

                mExec.execute(new SocketRunnable(clientSocket));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

            OutputStream out = null;
            InputStream in = null;

            try {

                out = mClient.getOutputStream();
                in = mClient.getInputStream();

                boolean flag = true;

                System.out.println("client ip:" + mClient.getInetAddress()  + "  port:" + mClient.getPort() + " come");

                MessageStream messageStream = new MessageStream();
                while (flag){

                    messageStream.receive(in);

                    Message message = messageStream.getMessage();
                    if (message == null){
                        continue;
                    }
                    processMessage(message, out);


                    //客户端发送的为普通消息,且表示结束会话
                    if(message.getType() == 0){
                        String msg = new String(message.getBody());
                        //客户端告知断开连接
                        if (msg.equals("bye")){
                            flag = false;
                        }
                    }
                }

            }catch (EOFException e){

            } catch (IOException e) {
                e.printStackTrace();
            }finally {

                if (out != null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mClient != null) {
                    try {
                        mClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("client ip:" + mClient.getInetAddress() + "  port:" + mClient.getPort() + " exit");
        }



        private void processMessage(Message message, OutputStream out) throws IOException{

            //客户端发送的为普通消息
            if(message.getType() == 0){

                String msg = new String(message.getBody());

                System.out.println("    client msg : " + msg);
                String answer = "echo " + msg;
                System.out.println("    server msg : " + answer);

                Message answerMessage = new Message((short) 0, answer.getBytes());
                answerMessage.send(out);
            }
            //文件消息
            else if (message.getType() == 1){

                System.out.println("    client msg : one file coming, receiving....");
            }


        }



        private void receiveFile(){

            try {


                InputStream in = mClient.getInputStream();
                OutputStream out =  new FileOutputStream("test.zip");

                byte[] bytes = new byte[1024 * 8];
                int count;
                while ((count = in.read(bytes))> 0){

                    out.write(bytes,0, count);
                }

                out.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
        }
    }
}
