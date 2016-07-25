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

        System.out.println("聊天开始: 请输入信息");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            boolean flag = true;

            while (flag){

                String str = reader.readLine();

                if (str == null){
                    break;
                }
                else if (str.equals("bye")){
                    //输入结束符
                    flag = false;

                }

                if (str.equals("file")){
                    sendFileMessage();

                }else {
                    sendNormalMessage(str);
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

        //关闭输出通道
        try {
            mClientSocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void receiveMessage(){

        try {

            boolean flag = true;
            MessageStream messageStream = new MessageStream();

            while (flag){

                messageStream.receive(mInputStream);

                Message message = messageStream.getMessage();
                if (message == null){
                    continue;
                }
                //处理到来的消息
                processCommMessage(message);

                //如果是普通消息, 且回话内容为"echo bye", 会话结束
                if(message.getType() == 0){

                    String msg = new String(message.getBody());
                    if (msg.equals("echo bye")){
                        flag  = false;
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Closing either the input or the output stream of a Socket closes the other stream and the Socket.
        try {
            mOutputStream.close();//关闭输出流
            mInputStream.close(); //关闭输入流
            mClientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("聊天关闭");
    }



    private void sendNormalMessage(String msg) throws IOException {

        //将读入的数据转换成约定的消息结构体
        Message message = new Message((short) 0, msg.getBytes());
        message.send(mOutputStream);
    }



    //发送文件
    private void sendFileMessage(){

        String filePath = this.getClass().getClassLoader().getResource("1111.zip").getPath();
        File file = new File(filePath);

        int length = (int) file.length();
        if (length < 0){
            return;
        }



        byte[] bytes = new byte[1024 * 8];
        int count;
        try {

            InputStream in = new FileInputStream(file);

            boolean firstPackage = true;
            while ( (count = in.read(bytes)) > 0){

                //剩余的数据,直接发送过去
                DataOutputStream dataOutputStream = new DataOutputStream(mOutputStream);
                dataOutputStream.write(bytes, 0, count);

            }

            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 处理消息
     * @param message Message
     */
    private void processCommMessage(Message message){

        //普通消息
        if(message.getType() == 0){

            String msg = new String(message.getBody());
            System.out.println("server: " + msg);
        }
        //文件消息
        else if(message.getType() == 1){
            System.out.println("server: one file coming, receiving...." );
        }

    }

}
