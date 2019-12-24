package com.dabay.logback.controller;

import com.dabay.logback.server.LogWebSocket;
import com.dabay.logback.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
 
/**
 * @Description:  消息发送类。服务端主动发送
 * @Author: Gentle
 * @date 2018/9/5  19:30
 */
@RestController
public class SocketController {
    @Resource
    LogWebSocket logWebSocket;

    @Autowired
    WebSocketService webSocketService;
 
    @RequestMapping("many")
    public String helloManyWebSocket(){
        //向所有人发送消息
        try {
            logWebSocket.sendMessageAll("你好~！");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "发送成功";
    }
 
    @RequestMapping("one")
    public String helloOneWebSocket(WebSocketSession session) throws IOException {
//    public String helloOneWebSocket(WebSocketSession session, String sessionId) throws IOException {
        //向某个人发送消息
        logWebSocket.sendMessageTo(session.getId(),"你好~！，单个用户");
        return "发送成功";
    }

    @RequestMapping("soutMessage")
    public String soutMessage(String loginName) throws IOException {
        webSocketService.sendLog(loginName);
        return "正在获取中";
    }
}