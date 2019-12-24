package com.dabay.logback.server;
 
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dabay.logback.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
 
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
 
/**
 * @Description: websocket简单入门。此类可以向个人发送消息。但是不适合向，因为这里无法适合业务。可以自行改造Map来控制
 * @Author: Gentle
 * @date 2018/9/5  18:43
 */
 
@Component
@ServerEndpoint(value = "/websocket/{loginName}")
@Slf4j
public class LogWebSocket {
    private static Long onlineNumber = 0L;
    /**
     * 每个客户端的session
     */
    private Session session;

    /**
     * 用户名
     */
    private String loginName;

    /**
     * 存放每个客户端对应的webSocket连接
     */
    private static Map<String, LogWebSocket> clients = new ConcurrentHashMap<>();
    /**
     * 打开连接。进入页面后会自动发请求到此进行连接
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("loginName") String loginName, Session session) {
        this.session = session;
        this.loginName = loginName;
        onlineNumber++;
        log.info("当前在线人数："+ onlineNumber);
        log.info("新连接的用户：" + loginName);
        clients.put(loginName, this);
    }
 
    /**
     * 用户关闭页面，即关闭连接
     */
    @OnClose
    public void onClose() {
        onlineNumber--;
        clients.remove(loginName);
        log.info(loginName + "websocket连接断开，当前在线人数"+ onlineNumber);
    }
 
    /**
     * 测试客户端发送消息，测试是否联通
     * @param message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        JSONObject jsonObject = JSONUtil.parseObj(message);
        String from = (String) jsonObject.get("from");
        String to = (String) jsonObject.get("to");
        String msg = (String) jsonObject.get("msg");

        try {
            if (to.equals("all")) {
                sendMessageAll(msg);
                return;
            }
            if (!clients.containsKey(to)) {
                sendMessageTo("发送的用户不在线。", from);
                return;
            }

            sendMessageTo("from " + from + ",msg:" + msg, to);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
 
    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：" + error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * 广播全部通知
     *
     * @param message
     * @throws IOException
     */
    public void sendMessageAll(String message) throws IOException {
        for (LogWebSocket item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    /**
     * 用于发送给指定客户端消息
     *
     * @param message
     */
    public void sendMessageTo(String toLoginName, String message) throws IOException {
        Session session = null;
        LogWebSocket tempWebSocket = null;
        for (LogWebSocket item : clients.values()) {
            if (item.loginName.equals(toLoginName)) {
//                item.session.getAsyncRemote().sendText(message);
                session = item.session;
                tempWebSocket = item;
                break;
            }
        }
        if (session != null) {
            tempWebSocket.session.getBasicRemote().sendText(message);
        } else {
            log.warn("没有找到你指定用户：{}", toLoginName);
        }
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
 
 
}