package com.imkk.udpSocketTalk;

import java.io.*;

/**
 * Created by kingwu on 8/3/16.
 *
 *  一个UDP的包大小不要超过一个MTU <1400 ;  ipv4(20) + udp(8) . 以太网MTU 1500,
 *
 *  用于定义 UDP通信过程中消息的结构,暂定类型为   分片(32) + 消息类型 (short)+ 消息体 (byte[])
 *
 *  分片的定义:采用ip分片的处理方式 Identification(16) + flag(3) + fragment offset(13)
 *  其中     Identification   :  唯一标识一个客户端发送过来的数据包,同一个分片的数据包这个值是相同的,  不同类型的数据包自动加1
 *          flag             :  0__ 保留不用; _0_标识这个包能被分段 _1_表示不可被分段;  __0标识是最后一个包, __1标识还有后续的包存在;
 *          fragment offset  :  片偏移,靠它来组包
 *
 *
 */
public class UdpMessage {
    public static final short NORMAL_MESSAGE = 0; //普通的消息
    public static final short USER_INO_MESSAGE = 1; //传递用户信息的 消息


    private short packetID;  //包的id
    private short sliceinfo; //分片信息
    private short type;
    private byte[] body;


    public UdpMessage(){
        type = 0;
        body = null;
    }

    public UdpMessage(short type, byte[] body){

        this.type = type;
        this.body = body;
    }


    // TODO: 8/4/16  这里面将基本数据类型转为 byte[] 和 将byte[]转为基本数据类型的方式并不好, 用stream会参数额外的byte,增加了包的大小
    public byte[] toBytes(){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeShort(type);
            out.writeObject(body);
            out.flush();
            out.close();
            byte[] msgBytes = bos.toByteArray();
            bos.close();

            return msgBytes;

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static UdpMessage bytesToUdpMessage(byte[] bytes, int length){
        try {
            UdpMessage udpMessage = new UdpMessage();


            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bin);

            udpMessage.setType(in.readShort());
            udpMessage.setBody( (byte[])in.readObject());

            bin.close();
            in.close();
            return udpMessage;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
