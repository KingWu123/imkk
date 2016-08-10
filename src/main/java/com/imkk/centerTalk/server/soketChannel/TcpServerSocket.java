package com.imkk.centerTalk.server.soketChannel;

import com.imkk.centerTalk.domain.TcpMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by kingwu on 8/10/16.
 *
 * 服务器端的socket 采用 响应-应答的方式, 即接受客户端的请求--处理请求--响应(类似于http)。
 *
 * 有以下功能
 * 1. 取出一条完成的消息
 * 2. 分发给消息处理器,得到响应的结果消息
 * 3. 进行结果的响应
 *
 * 4. 根据传过来的消息的链接模式(长连接 or 短连接),
 *      如果是端链接,则响应成功后,服务器直接断开链接。
 *      如果是长连接, 则不断开,但如果10min中内,没有任何消息来,则直接断开链接。
 *
 * 注:因为采用响应-应答的方式,所以客户端一次只能发送一条请求,发送完后,客户端等待响应。所以就不存在黏包的问题。
 */
public class TcpServerSocket implements Runnable {

    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public TcpServerSocket(Socket socket) throws IOException {

        mSocket = socket;

        mInputStream = socket.getInputStream();
        mOutputStream = socket.getOutputStream();
    }

    public void run() {

        try {
            while (true){

                //read inputStream, will block if not read whole one Message
                TcpMessage receivedMsg = receivingMessage(mInputStream);

                //process one Message
                TcpMessageProcessor processor = new TcpMessageProcessor(receivedMsg);
                TcpMessage responseMsg = processor.process();

                //response
                responseMessage(mOutputStream, responseMsg);

                //if short link, exit
                if (receivedMsg.getLinkType() == TcpMessage.NET_SHORT_LINK){
                    break;
                }else {
                    //if long link ,set read timeOut 10min
                    mSocket.setSoTimeout(10 * 1000);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        closeSocket();
    }


    /**
     * 接收数据
     * @param inputStream 输入流
     * @return 返回一条完成的tcpMessage
     * @throws IOException
     */
    private TcpMessage receivingMessage(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        short msgType = dataInputStream.readShort();
        byte linkType =  dataInputStream.readByte();
        int bodyLength = dataInputStream.readInt();
        int reservedBit = dataInputStream.readInt();

        byte[]body = new byte[bodyLength];
        int alreadyReadCount = 0;
        while (alreadyReadCount < bodyLength) {
            int count = dataInputStream.read(body, alreadyReadCount, bodyLength - alreadyReadCount);
            alreadyReadCount += count;
        }

        TcpMessage tcpMessage = new TcpMessage();
        tcpMessage.setMsgType(msgType);
        tcpMessage.setLinkType(linkType);
        tcpMessage.setBodyLength(bodyLength);
        tcpMessage.setReservedBit(reservedBit);
        tcpMessage.setBody(body);
        return tcpMessage;
    }


    /**
     * 响应结果消息
     * @param outputStream 输出流
     * @param responseMsg 响应消息
     * @throws IOException
     */
    private void responseMessage(OutputStream outputStream, TcpMessage responseMsg) throws IOException {
        responseMsg.send(outputStream);
    }


    /**
     * 关闭socket通信
     */
    private void closeSocket(){
        try{
            if (mOutputStream != null){
                mOutputStream.close();
            }
            if (mInputStream != null){
                mInputStream.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
