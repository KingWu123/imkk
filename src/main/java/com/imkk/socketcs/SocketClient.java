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

    PrintWriter mOutputStream;  //发送消息的 outputStream
    BufferedReader mInputStream; //接受数据的inputStream

    public SocketClient(){

        try {
            //1.创建套接字
            mClientSocket = new Socket(SERVER_IP, PORT);
            //mClientSocket.setSoTimeout(TIME_OUT);

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

        System.out.println("聊天开始: 请输入信息");
        while (flag){

            try {
                String str = reader.readLine();
                mOutputStream.println(str);

                //输入结束符
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

        //关闭输出通道
        try {
            mClientSocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void receiveMessage(){

        boolean flag = true;
        while (flag){
            try {
                String receiveMsg = mInputStream.readLine();

                if (receiveMsg == null ){
                    break;
                }

                //收到结束符
                if (receiveMsg.equals("echo bye")){
                    flag  = false;
                }
                //服务器传来接受文件,客户端传送一个文件过去
                else if (receiveMsg.equals("echo file")){

                    new Thread(new Runnable() {
                        public void run() {
                            sendFile();
                        }
                    }).start();
                }

                System.out.println("server: " + receiveMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }

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



    //发送文件
    private void sendFile(){

        String filePath = this.getClass().getClassLoader().getResource("1111.zip").getPath();
        File file = new File(filePath);

        long length = file.length();
        if (length < 0){
            return;
        }

        byte[] bytes = new byte[1024 * 8];
        int count;
        try {

            OutputStream out = mClientSocket.getOutputStream();
            InputStream in = new FileInputStream(file);

            while ( (count = in.read(bytes)) > 0){
                out.write(bytes, 0, count);
            }

            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
