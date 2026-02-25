package com.atjiao.cloud.ws;

import com.alibaba.fastjson2.JSON;
import com.atjiao.cloud.config.GetHttpSessionConfig;
import com.atjiao.cloud.domain.chatPo.Message;
import com.atjiao.cloud.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 焦叶鹏
 * * @data 2025/9/9 21:26
 * @description: 聊天端口
 **/
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
public class ChatEndpoint {

    // 保存在线的用户，key为用户名，value为 Session 对象
    private static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();

    private HttpSession httpSession;

    /**
     * 建立 websocket 连接后，被调用
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        String user = (String) this.httpSession.getAttribute("currentUser");
        if (user != null) {
            onlineUsers.put(user, session);
            log.info("用户[{}]已上线，当前在线人数: {}", user, onlineUsers.size());
        }

        // 通知所有用户，当前用户上线了
        broadcastAllUsers();
    }


    private Set<String> getFriends() {
        return onlineUsers.keySet();
    }

    private void broadcastAllUsers() {
        try {
            Set<String> friends = getFriends();
            Map<String, Object> map = new HashMap<>();
            map.put("system", true);
            map.put("fromName", null);
            map.put("message", friends);
            String message = JSON.toJSONString(map);
            log.info("广播在线用户列表: {}", message);

            // 发送给所有在线用户
            for (Map.Entry<String, Session> entry : onlineUsers.entrySet()) {
                Session session = entry.getValue();
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception exception) {
            log.error("广播消息失败", exception);
        }
    }



    /**
     * 浏览器发送消息到服务端时被调用，也就是私聊
     */
    @OnMessage
    public void onMessage(String message) {
        try {
            // 打印接收到的原始消息，用于调试
            log.info("接收到原始消息: {}", message);

            // 将消息推送给指定的用户
            Message msg = JSON.parseObject(message, Message.class);

            // 打印解析后的消息详情
            String fromUser = (String) this.httpSession.getAttribute("currentUser");
            log.info("用户[{}]发送给用户[{}]的消息: {}", fromUser, msg.getToName(), msg.getMessage());

            // 获取消息接收方的用户名
            String toName = msg.getToName();
            String tempMessage = msg.getMessage();

            // 获取消息接收方用户对象的 session 对象
            Session session = onlineUsers.get(toName);
            String currentUser = (String) this.httpSession.getAttribute("currentUser");
            String messageToSend = MessageUtils.getMessage(false, currentUser, tempMessage);

            session.getBasicRemote().sendText(messageToSend);
        } catch (Exception exception) {
            log.error("处理消息时发生错误", exception);
        }
    }

    /**
     * 断开 websocket 连接时被调用
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        String user = (String) this.httpSession.getAttribute("currentUser");
        if (user != null) {
            Session remove = onlineUsers.remove(user);
            if (remove != null) {
                remove.close();
            }
            session.close();
            log.info("用户[{}]已下线，当前在线人数: {}", user, onlineUsers.size());
        }

        // 通知所有用户，当前用户下线了
        broadcastAllUsers();
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误", error);
    }

    /**
     * 向指定用户发送消息（静态方法，供外部调用）
     * @param username 接收者用户名
     * @param message 消息内容
     * @return 是否发送成功
     */
    public static boolean sendMessageToUser(String username, String message) {
        try {
            Session session = onlineUsers.get(username);
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
                log.info("消息已发送给用户[{}]: {}", username, message);
                return true;
            } else {
                log.warn("用户[{}]不在线或会话已关闭", username);
                return false;
            }
        } catch (Exception e) {
            log.error("发送消息给用户[{}]失败", username, e);
            return false;
        }
    }

    /**
     * 获取在线用户列表（静态方法，供外部调用）
     * @return 在线用户名集合
     */
    public static Set<String> getOnlineUsers() {
        return onlineUsers.keySet();
    }
}
