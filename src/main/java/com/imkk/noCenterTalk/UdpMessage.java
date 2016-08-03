package com.imkk.noCenterTalk;

import java.io.*;

/**
 * Created by kingwu on 8/3/16.
 *
 *  用于定义 UDP通信过程中消息的结构,暂定类型为  消息类型 (short)+ 消息体 (byte[])
 */
public class UdpMessage {
    public static final short NORMAL_MESSAGE = 0; //普通的消息
    public static final short USER_INO_MESSAGE = 1; //传递用户信息的 消息

    private short type;
    private byte[] body;


    public UdpMessage(){
    }

    public UdpMessage(short type, byte[] body){

        this.type = type;
        this.body = body;
    }


    public byte[] toBytes(){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeShort(type);
            out.write(body);
            out.close();
            byte[] msgBytes = bos.toByteArray();
            bos.close();

            return msgBytes;

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static UdpMessage bytesToUdpMessage(byte[] bytes){
        try {
            UdpMessage udpMessage = new UdpMessage();


            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bin);

            udpMessage.setType(in.readShort());
            udpMessage.setBody(new byte[bytes.length - 2]);
            in.read(udpMessage.getBody());

            bin.close();
            in.close();
            return udpMessage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
