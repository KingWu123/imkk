package com.imkk.socketcs;

/**
 * Created by kingwu on 7/22/16.
 */
public class SocketProtocol {


    public static final int NORMAL_TYPE  = 1;
    public static final int FILE_TYPE = 2;

    /**
     * 在传送的消息加上一个协议type
     * @param msg 消息
     * @param type 类型, 1为普通消息, 2为文件
     */
    public static String msgAddProtocol(String msg, int type){
        String resultMsg = type + "|" + msg;
        return resultMsg;

    }

    public static String msgParseProtocol(String msg){
        int index = msg.indexOf("|");
        String resultMsg = msg.substring(index);
        return  resultMsg;
    }


    public static int getMsgType(String msg){
        int index = msg.indexOf("|");
        String typeStr = msg.substring(0, index);
        try {
            int type = Integer.valueOf(typeStr);
            return type;
        }catch (NumberFormatException e){
            return -1;
        }

    }
}
