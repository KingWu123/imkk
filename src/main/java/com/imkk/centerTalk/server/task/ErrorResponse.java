package com.imkk.centerTalk.server.task;

import com.alibaba.fastjson.JSONObject;
import com.imkk.centerTalk.domain.KwTcpMessage;
import com.imkk.centerTalk.server.soketLayer.ResponseMsgTypeConstant;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kingwu on 8/11/16.
 *
 * 所有的错误响应
 */
public class ErrorResponse {

    public static final int PREQUST_UNKOWN_ERROR = -1; //位置错误
    public static final int REQUEST_BODY_ERROR = 0;    //请求体错误


    public void tcpMsgResponse(OutputStream outputStream, int errCode){

        byte[] body = getErrorBody(errCode);

        KwTcpMessage.KwTcpMsgHeader responseHeader = new KwTcpMessage.KwTcpMsgHeader();
        responseHeader.setMsgType(ResponseMsgTypeConstant.RESPONSE_ERROR);
        responseHeader.setLinkType(KwTcpMessage.NET_UNKOWN_LINK);
        responseHeader.setBodyLength(body.length);

        KwTcpMessage response = new KwTcpMessage();
        response.setHeader(responseHeader);
        response.setBody(body);

        try {
            //send error response data
            response.send(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void udpMsgResponse(String ip, int port, int errCode){

    }





    private byte[] getErrorBody(int errorCode){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("errCode", errorCode);
        jsonObject.put("errStr", getErrorStr(errorCode));
        String jsonBodyStr = jsonObject.toJSONString();

        return jsonBodyStr.getBytes();
    }





    private String getErrorStr(int errCode){
        return "error error";
    }
}
