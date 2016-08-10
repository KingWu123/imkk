package com.imkk.noCenterTalk.domain;

/**
 * Created by kingwu on 8/10/16.
 */
public interface Message {

    //消息类型转为byte类型
    byte[] toBytes();

    //byte类型转为消息
    void toMessage(byte[] bytes);
}
