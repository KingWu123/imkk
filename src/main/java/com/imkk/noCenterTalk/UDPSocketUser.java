package com.imkk.noCenterTalk;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created by kingwu on 7/27/16.
 *
 * Udp 通信的 一个用户端。  随机bind一个本地的有效localIP 和 port
 *
 */
public class UDPSocketUser {


    private DatagramSocket mUserSocket;
    private UserData  mUserData;
    private BroadcastService mBroadcastService;
    private Set<UserData> mFriends = new HashSet<UserData>();
    private Thread mReceiveBroadcastFriendThread;

    private InetAddress mLocalHostAddress;


    public UDPSocketUser() {
        try {
            mLocalHostAddress = InetAddress.getLocalHost();
            mUserSocket = new DatagramSocket(0, mLocalHostAddress);

            createUserData();

            mBroadcastService = new BroadcastService();


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     * 加入广播组
     */
    public void joinGroup(){
        try {
            //加入用户广播组
            mBroadcastService.joinGroup();

            //接受其他用户广播事件
            receiveFriendsData();

            //发送当前用户上线的广播通知
            mBroadcastService.sendUserBroadcastData(mUserSocket, mUserData);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 关闭 udpsocket通信,关闭之前,发送用户下线的通知
     */
    public void close(){

        //发送用户下线的通知
        leaveGroup();
        mBroadcastService.close();
        mUserSocket.close();
    }


    /**
     * 离开广播组
     */
    private void leaveGroup(){
        try {
            //用户为离线状态,发送用户离线状态通知
            mUserData.setNetWorkState(UserData.NetWorkState.USER_OFFLINE);
            mBroadcastService.sendUserBroadcastData(mUserSocket, mUserData);


            //接受广播线程终端掉, 不再接受广播事件
            mReceiveBroadcastFriendThread.interrupt();

            //离开广播组
            mBroadcastService.leaveGroup();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 接受广播来的其他用户数据
     */
    private void receiveFriendsData(){

        mReceiveBroadcastFriendThread = new Thread(new Runnable() {
            public void run() {

                try {
                    while (!Thread.interrupted()) {
                        UserData friend = mBroadcastService.receiveUserBroadcastData();

                        //过滤掉自己发给自己的广播
                        if (friend.getUserIP().equals(mUserData.getUserIP())
                                && friend.getUserPort() == mUserData.getUserPort()) {
                            continue;
                        }

                        addFriend(friend);
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mReceiveBroadcastFriendThread.start();
    }



    /**
     * 添加一个 朋友
     * @param newFriend userData
     */
    synchronized private  void addFriend(UserData newFriend){

        //先查找这个friend用户是否存在,如果存在了,先删除掉。
        Iterator<UserData> iterator = mFriends.iterator();
        UserData containedFriend = null;
        while (iterator.hasNext()){
            UserData tempUserData = iterator.next();
            if (tempUserData.compareTo(newFriend) == 0){
                containedFriend = tempUserData;
                break;
            }
        }
        if (containedFriend != null){
            mFriends.remove(containedFriend);
        }

        mFriends.add(newFriend);
    }

    /**
     * 根据用户id一个 朋友
     * @param userId
     * @return
     */
    synchronized private  UserData getFriendById(String userId){

        Iterator<UserData> iterator = mFriends.iterator();
        UserData containedFriend = null;
        while (iterator.hasNext()){
            UserData tempUserData = iterator.next();
            if (tempUserData.getUserId().equals(userId)){
                containedFriend = tempUserData;
                break;
            }
        }
        return containedFriend;
    }




    /**
     *  发送一条消息
     * @param host 远端的 地址
     * @param port     远端应用端口
     * @param sendBytes 发送的内容
     * @return 发送数据是否成功
     */
    public boolean sendMessage(String host, int port, byte[]sendBytes) {

        try {

            InetAddress address = InetAddress.getByName(host);
            DatagramPacket sendPacket
                    = new DatagramPacket(sendBytes , 0 ,sendBytes.length , address , port);
            mUserSocket.send(sendPacket);
            return true;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public DatagramPacket receiveMessage() throws IOException{

        byte[] buffer = new byte[1024 * 16];
        DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);

        mUserSocket.receive(receivePacket);

        //receivePacket.getAddress();
        //receivePacket.getPort();

        return receivePacket;
    }


    @Override
    public String toString() {
        return "ip: " + mUserSocket.getLocalAddress() + " port: " + mUserSocket.getLocalPort();
    }


    //生成用户数据
    private void createUserData(){
        long id = System.currentTimeMillis();
        this.mUserData = new UserData();

        this.mUserData.setUserId("" + id);
        this.mUserData.setUserName("user" + id);
        this.mUserData.setUserIP(mUserSocket.getLocalAddress().getHostAddress());
        this.mUserData.setUserPort(mUserSocket.getLocalPort());
        this.mUserData.setNetWorkState(UserData.NetWorkState.USER_ONLINE);

    }

}
