package com.imkk.centerTalk.server.task;

import com.imkk.centerTalk.domain.KwTcpMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kingwu on 8/11/16.
 *
 * 所有处理任务的 虚基类
 */
public abstract class Task {


  /**
   * tcp消息 处理, 包含结果的响应
   * @param header 消息头
   * @param inputStream 输入流
   * @param outputStream 输出流
     */
  abstract public void tcpMsgProcess(KwTcpMessage.KwTcpMsgHeader header, InputStream inputStream, OutputStream outputStream);






  /**
   * 读取tcp消息 消息体
   * @param bodyLength body 长度
   * @param inputStream 输入流
   * @return 返回消息体, 如果消息体读取失败,放回null
   */
  public byte[] readTcpMsgBody(int bodyLength, InputStream inputStream){

    DataInputStream dataInputStream = new DataInputStream(inputStream);
    byte[]body = new byte[bodyLength];
    int alreadyReadCount = 0;

    try {
      while (alreadyReadCount < bodyLength) {
        int count = dataInputStream.read(body, alreadyReadCount,  bodyLength - alreadyReadCount);
        alreadyReadCount += count;
      }
    }catch (IOException e){
      return null;
    }

    return body;
  }


}
