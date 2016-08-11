package com.imkk.centerTalk.server.task;

import com.alibaba.fastjson.JSON;
import com.imkk.centerTalk.domain.KwTcpMessage;
import com.imkk.centerTalk.server.soketLayer.ResponseMsgTypeConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kingwu on 8/11/16.
 *
 *  登录的任务
 */
public class LoginTask extends Task{

    private LoginData loginData;
    private byte[] responseBody;

    public LoginTask(){
    }


    //tcp消息请求处理
    public void tcpMsgProcess(KwTcpMessage.KwTcpMsgHeader header, InputStream inputStream, OutputStream outputStream) {

        byte[] body = readTcpMsgBody(header.getBodyLength(), inputStream);

        if(body == null){
            //发送消息体错误的结果响应
            new ErrorResponse().tcpMsgResponse(outputStream, ErrorResponse.REQUEST_BODY_ERROR);
            return;
        }


        String jsonStr = new String(body);
        LoginTask.LoginData loginData = JSON.parseObject(jsonStr, LoginTask.LoginData.class);

        this.loginData = loginData;

        byte[] responseBody = checkLogin();

        //结果响应
        loginTcpMsgResponse(responseBody, outputStream);

    }


    //tcp结果的响应
    private void loginTcpMsgResponse(byte[]responseBody, OutputStream outputStream) {

        KwTcpMessage.KwTcpMsgHeader responseHeader = new KwTcpMessage.KwTcpMsgHeader();
        responseHeader.setMsgType(ResponseMsgTypeConstant.RESPONSE_LOGIN);
        responseHeader.setLinkType(KwTcpMessage.NET_UNKOWN_LINK);
        responseHeader.setBodyLength(responseBody.length);

        KwTcpMessage response = new KwTcpMessage();
        response.setHeader(responseHeader);
        response.setBody(responseBody);

        try {
            //send response data
            response.send(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //验证登录
    private byte[] checkLogin(){
        return "login success".getBytes();
    }





    public static class LoginData{

        private String userId;
        private String userName;
        private String password;
        private String talkIp; //客户端专用来聊天的ip
        private int talkPort;  //客户端专用来聊天的port,


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTalkIp() {
            return talkIp;
        }

        public void setTalkIp(String talkIp) {
            this.talkIp = talkIp;
        }

        public int getTalkPort() {
            return talkPort;
        }

        public void setTalkPort(int talkPort) {
            this.talkPort = talkPort;
        }
    }
}
