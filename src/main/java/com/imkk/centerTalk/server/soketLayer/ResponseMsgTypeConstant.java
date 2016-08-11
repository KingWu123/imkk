package com.imkk.centerTalk.server.soketLayer;

/**
 * Created by kingwu on 8/11/16.
 *
 * 定义响应消息的类型, 都是 < 0
 */
public class ResponseMsgTypeConstant {

    public static final short RESPONSE_ERROR = -1; //错误消息的响应

    public static final short RESPONSE_LOGIN = -2; //登录消息的响应
}
