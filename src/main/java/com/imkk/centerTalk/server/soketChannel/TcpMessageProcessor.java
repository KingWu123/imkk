package com.imkk.centerTalk.server.soketChannel;

import com.imkk.centerTalk.domain.TcpMessage;

/**
 * Created by kingwu on 8/10/16.
 *
 * 用于处理tcpMessage, 根据消息内容,进行响应的处理
 */
public class TcpMessageProcessor {

    private TcpMessage mReceiveMessage;

    public TcpMessageProcessor(TcpMessage receiveMessage){
        mReceiveMessage = receiveMessage;
    }

    public TcpMessage process(){
        TcpMessage responseMsg = new TcpMessage();

        return responseMsg;
    }
}
