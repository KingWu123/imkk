package com.imkk.socketcs;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kingwu on 7/25/16.
 *
 * socket 通信 消息协议的 统一定义
 *
 * 一条消息由:   包头标记位 + 消息类型 + 包体长度 + 包体  组成。
 *
 * 提供了一个发送消息的 接口 {@link #send(OutputStream)}
 *
 */



public class Message {

    public static final int PACKAGE_FLAG = 163163163;  //包头 起始标记位
    private short type;      //消息类型
    private int bodyLength;  //包体长度
    private byte[] body;     //包体内容


    public short getType() {
        return type;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public byte[] getBody() {
        return body;
    }

    //包的总长度
    public int getTotalLength(){
        return 4 + 2 + 4 + bodyLength;
    }

    public Message(){

        this((short)0, null);
    }

    public Message(short type, byte[] body){

        this.type = type;
        this.bodyLength = body != null ? body.length : 0;
        this.body = body;
    }


    public void send(OutputStream outputStream) throws  IOException{

        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));

        dataOutputStream.writeInt(PACKAGE_FLAG);
        dataOutputStream.writeShort(this.type);
        dataOutputStream.writeInt(this.bodyLength);
        if(this.bodyLength > 0) {
            dataOutputStream.write(this.body);
        }

        dataOutputStream.flush();
    }

}
