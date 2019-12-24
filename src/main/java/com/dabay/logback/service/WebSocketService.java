package com.dabay.logback.service;


import java.io.IOException;

public interface WebSocketService {

    /*向前端发送日志*/
    void sendLog(String sessionid) throws IOException;
}