package com.imkk.noCenterTalk;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created by kingwu on 7/27/16.
 *
 * Udp 通信的 一个用户端。  随机bind一个本地的有效localIP 和 port
 *
 * 实现局域网内无中心服务的聊天功能. 实现过程如下:
 * 1. 每个User会用 {@see BroadcastService}创建一个广播MulticastSocket, 使用这个广播socket将自身的用户信息(包括ip/port等)广播出去。
 * 2. 当局域网内 加入组播的用户收到这个广播通知后,就在自己的用户表里记录下这个用户信息。
 *         同时向这个用户的ip/port发送一条自身的信息 (注意:这个过程不是广播,而是单播通信)
 *
 * 这样每个用户就能维护一个局域网所有用户的信息表,知道对方的ip/port。 可以有选择的进行tcp/udp通信了。当用户的上线状态改变时,也用此流程处理
 *
 * 上面的方案类似于ARP的处理方式。
 *
 */
public class UdpSocketUser {


    private DatagramSocket mUserSocket;
    private UserData  mUserData;
    private BroadcastService mBroadcastService;
    private Set<UserData> mFriends = new HashSet<UserData>();
    private Thread mReceiveBroadcastFriendThread;

    private InetAddress mLocalHostAddress;


    public UdpSocketUser() {
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
     * 加入广播组, 同时将自身的用户信息广播出去, 并监听组播内的广播
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

                        //缓存到用户列表
                        addFriend(friend);

                        //收到广播后,同时给广播方发送一条告知身份的用户数据,告诉自己的地址信息
                        UdpMessage udpMessage = new UdpMessage(UdpMessage.USER_INO_MESSAGE, mUserData.toBytes());
                        sendMessage(friend.getUserIP(), friend.getUserPort(), udpMessage);
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mReceiveBroadcastFriendThread.start();
    }



    /**
     *  发送一条消息
     * @param host 远端的 地址
     * @param port     远端应用端口
     * @param udpMessage 发送的Message内容
     * @return 发送数据是否成功
     */
    public boolean sendMessage(String host, int port, UdpMessage udpMessage) throws IOException {

        byte[]sendBytes = udpMessage.toBytes();
        InetAddress address = InetAddress.getByName(host);
        DatagramPacket sendPacket
                    = new DatagramPacket(sendBytes , 0 ,sendBytes.length , address , port);
        mUserSocket.send(sendPacket);
        return true;

    }


    public UdpMessage receiveMessage() throws IOException{

        byte[] buffer = new byte[1024 * 16];
        DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);

        mUserSocket.receive(receivePacket);

        UdpMessage udpMessage = UdpMessage.bytesToUdpMessage(receivePacket.getData(),receivePacket.getLength());

        //如果过来的是对方的用户数据, 这里截断掉
        if (udpMessage.getType() == UdpMessage.USER_INO_MESSAGE){

            UserData userData = UserData.userData(udpMessage.getBody(), receivePacket.getLength());

            //不管用户自己填的是什么ip/port,这里都已包里面的ip/port为准
            InetAddress remoteAddress =  receivePacket.getAddress();
            int remotePort = receivePacket.getPort();
            userData.setUserIP(remoteAddress.getHostAddress());
            userData.setUserPort(remotePort);

            addFriend(userData);

            return null;
        }

        System.out.print("msg from friend " + receivePacket.getAddress() + "/" + receivePacket.getPort() + ": ");
        return udpMessage;
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

        System.out.println("-------friends list:-------\n" + mFriends);
        System.out.println("---------------------------");
    }

    /**
     * 根据用户id一个 朋友
     * @param userId
     * @return
     */
    synchronized public  UserData getFriendById(String userId){

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

    @Override
    public String toString() {
        return "ip: " + mUserSocket.getLocalAddress() + " port: " + mUserSocket.getLocalPort();
    }



}
