package com.atjiao.cloud.config;

/**
 * @author 焦叶鹏
 * * @data 2025/9/10 08:51
 * @description: TODO
 **/

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 获取HttpSession，这样的话，ChatEndpoint类就能操作HttpSession
 */
public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        System.out.println("请求头中的 Cookie: " + request.getHeaders().get("Cookie"));
        if (httpSession == null) {
            System.err.println("HttpSession 为 null");
        } else {
            System.out.println("HttpSession ID: " + httpSession.getId());
            System.out.println("currentUser: " + httpSession.getAttribute("currentUser"));
        }
        config.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
