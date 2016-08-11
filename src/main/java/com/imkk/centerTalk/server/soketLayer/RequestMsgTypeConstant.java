package com.imkk.centerTalk.server.soketLayer;

/**
 * Created by kingwu on 8/11/16.
 *
 *  定义请求消息的类型, 根据不同的消息类型,进行相应的处理,  都是  > 0
 */
public class RequestMsgTypeConstant {

    public static final short REQUEST_LOGIN = 0;//用户身份登录,目前的登录

    public static final short REQUEST_FRIEND_LIST = 1;//获取用户信息列表

    public static final short REQUEST_SEND_MSG_TO_FRIEND = 2;//发送消息给朋友

}
