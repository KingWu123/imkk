package com.imkk.noCenterTalk;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.*;

/**
 * Created by kingwu on 8/1/16.
 */
public class UserData  implements Comparable<UserData>{



    public static enum NetWorkState{
        USER_ONLINE,  //上线
        USER_OFFLINE, //离线
        USER_HIDDEN,  //隐身
    }

    private String userId;
    private String userName;
    private String userIP;
    private int userPort;
    private NetWorkState netWorkState;



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public int getUserPort() {
        return userPort;
    }

    public void setUserPort(int userPort) {
        this.userPort = userPort;
    }


    public NetWorkState getNetWorkState() {
        return netWorkState;
    }

    public void setNetWorkState(NetWorkState mNetWorkState) {
        this.netWorkState = mNetWorkState;
    }


    /**
     * 将用户数据转为bytes数据返回
     * @return 用户bytes数据
     */
    public byte[] getBytes(){

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeUTF(userId);
            out.writeUTF(userName);
            out.writeUTF(userIP);
            out.writeInt(userPort);
            out.writeInt(netWorkState.ordinal());
            out.close();

            byte[] bytes = bos.toByteArray();
            bos.close();

            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 将byte[] 类型数据转为一个用户数据
     * @param bytes
     * @return
     */
    public static UserData userData(byte[] bytes){

        try {
            UserData userData ;
            userData = new UserData();

            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bin);

            userData.setUserId(in.readUTF());
            userData.setUserName(in.readUTF());
            userData.setUserIP(in.readUTF());
            userData.setUserPort(in.readInt());
            userData.setNetWorkState(NetWorkState.values()[in.readInt()]);

            return userData;
        } catch (IOException e) {
            e.printStackTrace();
        }

       return null;
    }

    public int compareTo(UserData o) {
        if ((this.getUserId().equals(o.getUserId()))){
            return 0;
        }else {
            return -1;
        }
    }

}
