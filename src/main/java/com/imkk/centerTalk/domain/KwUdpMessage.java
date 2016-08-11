package com.imkk.centerTalk.domain;

/**
 * Created by kingwu on 8/11/16.
 *
 * 一个UDP的包大小不要超过一个MTU <1400 ;  ipv4(20) + udp(8) . 以太网MTU 1500,
 *
 *  用于定义 UDP通信过程中消息的结构   分片(32) + 消息类型 (short)+ 消息体 (byte[])
 *
 *  分片的定义:采用ip分片的处理方式 Identification(16) + flag(3) + fragment offset(13)
 *  其中     Identification   :  唯一标识一个客户端发送过来的数据包,同一个分片的数据包这个值是相同的,  不同类型的数据包自动加1
 *          flag             :  0__ 保留不用; _0_标识这个包能被分段 _1_表示不可被分段;  __0标识是最后一个包, __1标识还有后续的包存在;
 *          fragment offset  :  片偏移,靠它来组包
 *
 */
public class KwUdpMessage {

    private kwUdpHeader header;
    private byte[] body;


    public static class kwUdpHeader{
        int segmentation;
        short msgType;
    }
}
