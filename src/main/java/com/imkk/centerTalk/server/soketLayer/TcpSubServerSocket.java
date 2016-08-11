package com.imkk.centerTalk.server.soketLayer;

import com.imkk.centerTalk.domain.KwTcpMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by kingwu on 8/10/16.
 *
 * 服务器端的socket 采用 响应-应答的方式, 即接受客户端的请求--处理请求--响应(类似于http)。
 *
 * 步骤如下
 * 1. 取出一条协议消息头
 * 2. 由消息分发器根据消息类型,分发给具体的任务去执行
 *
 * 3. 根据传过来的消息的链接模式(长连接 or 短连接),
 *      如果是端链接,则响应成功后,服务器直接断开链接。
 *      如果是长连接, 则不断开,但如果10min中内,没有任何消息来,则直接断开链接。
 *
 * 注:因为采用响应-应答的方式,所以客户端一次只能发送一条请求,发送完后,客户端等待响应。所以就不存在黏包的问题。
 */
public class TcpSubServerSocket implements Runnable {

    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    private KwTcpMsgDistributor mTcpMsgDistributor;

    public TcpSubServerSocket(Socket socket) throws IOException {

        mSocket = socket;

        mInputStream = socket.getInputStream();
        mOutputStream = socket.getOutputStream();

        mTcpMsgDistributor = new KwTcpMsgDistributor();
    }



    public void run() {

        try {
            while (true){

                //read inputStream, will block if not read whole whole KwTcpMsgHeader
                KwTcpMessage.KwTcpMsgHeader header = new KwTcpMessage.KwTcpMsgHeader();
                header.streamToHeader(mInputStream);

                //处理消息,根据不同消息,会分发给不同的消息处理对象,在由这些处理对象 进行结果的响应
                mTcpMsgDistributor.distribute(header, mInputStream, mOutputStream);

                //if long link ,set read timeOut 10min
                if (header.getLinkType() == KwTcpMessage.NET_LONG_LINK){
                    mSocket.setSoTimeout(10 * 1000);

                }else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        closeSocket();
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
