package com.imkk.socketcs;

import java.io.*;
import java.net.Socket;

/**
 * Created by kingwu on 7/18/16.
 *
 * 实现 socket通信的客户端
 *
 *
 * 1. Open a socket.
 * 2. Open an input stream and output stream to the socket.
 * 3. Read from and write to the stream according to the server's protocol.
 * 4. Close the streams.
 * 5. Close the socket.
 */
public class SocketClient {

    private static final String SERVER_IP = "localhost";//"10.240.252.97";
    private static final int PORT = 65534;
    private static final int TIME_OUT = 10 * 1000;//10S

    private Socket mClientSocket;

    OutputStream mOutputStream;  //发送消息的 outputStream
    InputStream mInputStream; //接受数据的inputStream

    public SocketClient(){

        try {
            //1.创建套接字
            mClientSocket = new Socket(SERVER_IP, PORT);
            //mClientSocket.setSoTimeout(TIME_OUT);

            //2.通过套接字的I/O流与服务端通信
            mOutputStream = mClientSocket.getOutputStream();
            mInputStream = mClientSocket.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //建立聊天通信
    public void communicate(){

        if (mClientSocket != null && mClientSocket.isConnected()) {
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
        }else {
            System.out.println("socket not connect success");
        }
    }


    private void sendMessage(){

        System.out.println("聊天开始: 请输入信息");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {

            while (true){

                String str = reader.readLine();

                if (str == null){
                    break;
                } else if (str.equals("bye")){
                    break;
                }else if (str.equals("")){
                    continue;
                }

                if (str.equals("file")){

                    //发送需要传文件的消息
                    Message message = new Message((short) 1, str.getBytes());
                    message.send(mOutputStream);
                }else {
                    //发送需要传文件的消息
                    Message message = new Message((short) 0, str.getBytes());
                    message.send(mOutputStream);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //关闭键盘输入
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //关闭socket
        closeSocket();

        System.out.println("聊天关闭");

        //关闭输出通道的方法
//        try {
//            mClientSocket.shutdownOutput();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }



    private void receiveMessage(){

        try {

            MessageStream messageStream = new MessageStream();

            while (true){

                int count = messageStream.receive(mInputStream);
                Message message = messageStream.getMessage();

                //如果count = -1 (remote socket closed), 且没有解析出消息, 则直接退出
                if (count == -1 && message == null){
                    break;
                } else  if (message == null){
                    continue;
                }

                //处理到来的消息
                processComeMessage(message);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        closeSocket();
    }

    

    /**
     * 处理消息
     * @param message Message
     */
    private void processComeMessage(Message message){

        String responseMsg = "";
        if (message.getBody() != null) {
            responseMsg = new String(message.getBody());
        }


        //普通消息
        if(message.getType() == 0){
            System.out.println("server: " + responseMsg);
        }
        //其他消息
        else {
            // TODO: 7/30/16
        }


        //对方接受文件
        if(responseMsg.equals("echo file")){
            String filePath = this.getClass().getClassLoader().getResource("1111.zip").getPath();
            FileUtil.sendFile(filePath, mOutputStream);
        }

    }





    private void closeSocket(){
        // Closing either the input or the output stream of a Socket closes the other stream and the Socket.
        try {
            mOutputStream.close();//关闭输出流
            mInputStream.close(); //关闭输入流
            mClientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
