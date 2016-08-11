package com.imkk.centerTalk.server.soketLayer;

import com.imkk.centerTalk.domain.KwTcpMessage;
import com.imkk.centerTalk.server.task.LoginTask;
import com.imkk.centerTalk.server.task.Task;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kingwu on 8/10/16.
 *
 * kwTcpMessage消息 分发中心,根据不同的消息类型, 分发给不同的任务去处理
 */
public class KwTcpMsgDistributor {

    public KwTcpMsgDistributor(){
    }


    /**
     *  根据不同的消息类型, 进行任务分发处理
     */
    public void distribute(KwTcpMessage.KwTcpMsgHeader header, InputStream inputStream, OutputStream outputStream){

        switch (header.getMsgType()){
            case RequestMsgTypeConstant.REQUEST_LOGIN:
                loginDistributor(header, inputStream, outputStream);
                break;
            case RequestMsgTypeConstant.REQUEST_FRIEND_LIST:
                break;
            case RequestMsgTypeConstant.REQUEST_SEND_MSG_TO_FRIEND:
                break;

            default:
                break;
        }

    }


    private void  loginDistributor(KwTcpMessage.KwTcpMsgHeader header, InputStream inputStream, OutputStream outputStream){

        Task loginTask = new LoginTask();

        loginTask.tcpMsgProcess(header, inputStream, outputStream);
    }






}
