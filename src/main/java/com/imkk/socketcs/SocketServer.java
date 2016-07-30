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


    /**
     * 处理两种消息, 一种普通的 用Message对象来表达的消息, 一种文件消息。默认的是从接受普通消息开始的。
     * 在接受文件消息时,需要先收到一个普通的Message消息,告知有文件将要到来, 然后 输入流的处理过程 切换到文件处理流程。
     */
    private static class SocketRunnable implements Runnable{

        private Socket mClient = null;
        private boolean isFileComing; //文件是否到来

        private InputStream   mInputStream;
        private OutputStream  mOutputStream;
        private MessageStream mMessageStream;

        public SocketRunnable(Socket client) throws IOException {
            mClient = client;
            isFileComing = false;

            mOutputStream = mClient.getOutputStream();
            mInputStream = mClient.getInputStream();
            mMessageStream = new MessageStream();
        }

        public void run() {

            System.out.println("client ip:" + mClient.getInetAddress()  + "  port:" + mClient.getPort() + " come");

            boolean flag = true;
            while (flag){

                try {
                    if(!isFileComing) {

                        //方法里面如果发现 下一条 消息是文件消息,会改变 isFileComing=true。 切换到文件消息的处理流程。
                        normalMessageCome(mMessageStream, mInputStream, mOutputStream);

                    }else {

                        fileMessageCome(mInputStream, mOutputStream);
                    }

                }catch (Exception e){

                    flag = false;
                }

            }


            closeSocket();


            System.out.println("client ip:" + mClient.getInetAddress() + "  port:" + mClient.getPort() + " exit");
        }




        /**
         *  处理普通消息的到来
         * @param messageStream  MessageStream对象
         * @param in             输入流
         * @param out            输出流
         * @return               是否成功处理了一条消息
         * @throws IOException
         */
        private boolean normalMessageCome(MessageStream messageStream, InputStream in, OutputStream out) throws IOException {


            int count = messageStream.receive(in);

            Message message = messageStream.getMessage();

            //如果count = -1(表示客户端已经关闭了socket,没有数据可读了)。 且没有解析出消息。 抛出io异常
            if (count == -1 && message == null) {
                throw new IOException("remote socket closed and no message to process");
            } else if (message == null) {
                return false;
            }

            normalMessageResponse(message, out);



            //客户端表示自己想传文件,则切换到文件传输方式
            if (message.getType() == 1){
                isFileComing = true;
            }

            return true;
        }


        //普通消息应答
        private void normalMessageResponse(Message message, OutputStream out){


            if (message.getBody() == null){
                return;
            }
            String msg = new String(message.getBody());

            //客户端发送的为普通消息
            System.out.println("    client msg : " + msg);
            String answer = "echo " + msg;
            System.out.println("    server msg : " + answer);

            Message answerMessage = new Message((short) 0, answer.getBytes());

            try {
                answerMessage.send(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        private void fileMessageCome(InputStream inputStream, OutputStream outputStream) throws IOException{
            //处理接受文件
            FileUtil.receiveFile("dest.zip", inputStream);

            //文件
            isFileComing = false;
        }




        private void closeSocket(){
            if (mOutputStream != null){
                try {
                    mOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mInputStream != null){
                try {
                    mInputStream.close();
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
    }
}
