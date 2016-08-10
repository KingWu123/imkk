package com.imkk.centerTalk.domain;

import java.io.*;

/**
 * Created by kingwu on 8/10/16.
 *
 * 客户端与服务器端定义的一套 用于通信的 Tcp消息协议
 *
 * 协议为:  消息类型(short) + 链接模式(byte) + body长度  + body
 *
 */
public class TcpMessage {


    public static final int  NET_LONG_LINK = 1; //长连接 默认长链接
    public static final int  NET_SHORT_LINK = 2; //端链接

    private short msgType;
    private byte linkType = 1;
    private int bodyLength;
    private int  reservedBit;
    private byte[] body;



    public void send(OutputStream outputStream) throws IOException {

        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));

        dataOutputStream.writeShort(msgType);
        dataOutputStream.writeByte(linkType);
        dataOutputStream.writeInt(bodyLength);
        dataOutputStream.writeInt(reservedBit);
        if(this.bodyLength > 0) {
            dataOutputStream.write(body);
        }
        dataOutputStream.flush();
    }


    public short getMsgType() {
        return msgType;
    }

    public void setMsgType(short msgType) {
        this.msgType = msgType;
    }

    public byte getLinkType() {
        return linkType;
    }

    public void setLinkType(byte linkType) {
        this.linkType = linkType;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public int getReservedBit() {
        return reservedBit;
    }

    public void setReservedBit(int reservedBit) {
        this.reservedBit = reservedBit;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
