package com.imkk.tcpSocketTalk;

import java.io.*;

/**
 * Created by kingwu on 7/26/16.
 *
 * 用于处理 文件消息
 */
public class FileUtil {

    //发送文件
    public static void sendFile(String filePath, OutputStream outputStream){

        File file = new File(filePath);

        int length = (int) file.length();
        if (length < 0){
            return;
        }

        System.out.println("begin sending file....");
        int count;
        try {

            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));

            //发送包头
            dataOutputStream.writeInt(Message.PACKAGE_FLAG);
            dataOutputStream.writeShort(1);
            dataOutputStream.writeInt(length);

            InputStream in = new FileInputStream(file);
            byte[] bytes = new byte[1024 * 4];
            while ( (count = in.read(bytes)) > 0){
                dataOutputStream.write(bytes, 0, count);
                dataOutputStream.flush();
            }

            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("end sending file....");

    }


    public static void receiveFile(String savePath, InputStream inputStream) throws IOException{

        System.out.println("begin receiveFile file....");


        OutputStream out = null;
        try {
            out = new FileOutputStream(savePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        DataInputStream in = new DataInputStream(inputStream);
        int packageFlag = in.readInt();
        int packageType = in.readShort();
        int fileLength = in.readInt();
        //可以对上面的3信息做校验

        byte[] bytes = new byte[1024 * 8];
        int totalLength = 0;
        int count;
        while ((count = in.read(bytes))> 0){

            try {
                out.write(bytes,0, count);
            } catch (IOException e) {
                e.printStackTrace();
            }

            totalLength += count;
            if (totalLength == fileLength){
                break;
            }
        }



        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("end receiveFile file....");
    }






    public static void saveFile(String filePath, Message message){
        System.out.println("save receiveFile file....");
        try {
            if (message.getBodyLength() > 0) {

                OutputStream out = new FileOutputStream(filePath);
                out.write(message.getBody(), 0, message.getBodyLength());
                out.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("end receiveFile file....");
    }

}
