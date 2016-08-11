package com.imkk.centerTalk.domain;

import java.io.*;

/**
 * Created by kingwu on 8/10/16.
 *
 * 客户端与服务器端定义的一套 基于tcp通信的 消息协议,
 *
 * 协议为:  header + body(byte[])
 *         header = 消息类型(short) + 链接模式(byte) + body长度(int) + 保留位(int)
 *
 * 注: 这里"客户端请求"和"服务器响应"用了同一套协议, 服务器填写的链接模式 客户端忽略
 */
public class KwTcpMessage {

    public static final byte  NET_UNKOWN_LINK = 0; //位置链接模式
    public static final byte  NET_LONG_LINK = 1; //长连接 默认长链接
    public static final byte  NET_SHORT_LINK = 2; //端链接


    private KwTcpMsgHeader header;
    private byte[] body;


    public void send(OutputStream outputStream) throws IOException {

        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));

        dataOutputStream.writeShort(header.msgType);
        dataOutputStream.writeByte(header.linkType);
        dataOutputStream.writeInt(header.bodyLength);
        dataOutputStream.writeInt(header.reservedBit);
        if(this.header.bodyLength > 0) {
            dataOutputStream.write(body);
        }
        dataOutputStream.flush();
    }


    public KwTcpMsgHeader getHeader() {
        return header;
    }

    public void setHeader(KwTcpMsgHeader header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }



    public static class KwTcpMsgHeader {
        private short msgType;
        private byte linkType = NET_LONG_LINK;
        private int bodyLength;
        private int  reservedBit;


        /**
         * 接收数据, 读取完成的 KwTcpMsgHeader
         * @param inputStream 输入流
         * @return 返回一条完成的 KwTcpMsgHeader
         * @throws IOException
         */
        public void streamToHeader(InputStream inputStream) throws IOException {
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            short msgType = dataInputStream.readShort();
            byte linkType =  dataInputStream.readByte();
            int bodyLength = dataInputStream.readInt();
            int reservedBit = dataInputStream.readInt();

            setMsgType(msgType);
            setLinkType(linkType);
            setBodyLength(bodyLength);
            setReservedBit(reservedBit);
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

    }


}
