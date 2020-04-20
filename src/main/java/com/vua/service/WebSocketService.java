package com.vua.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @program: websocket
 * @description:
 * @author: vua
 * @create: 2020-04-18 18:37
 */
@Service
@ServerEndpoint("/websocket/{name}")
public class WebSocketService {
    private static String NAME = "name";
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final List<String> onlines = new CopyOnWriteArrayList<>();
    private Session curr_session;
    private String name;

    @OnOpen
    public void open(Session session, @PathParam("name") String name) {
        this.curr_session = session;
        this.name = session.getPathParameters().get(NAME);
        this.name = name;
        onlines.add(name);
        sessions.add(session);
        sendMessage( JSON.toJSONString(onlines),false);
    }

    @OnClose
    public void close() {
        onlines.remove(name);
        sessions.remove(curr_session);
        sendMessage( JSON.toJSONString(onlines),false);
    }

    @OnMessage
    public void onMessage(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("mess", message);
        sendMessage(JSON.toJSONString(map), true);
    }

    public void sendMessage(String message, boolean exceptU) {
        for (Session session : sessions) {
            if (exceptU && session == curr_session) continue; //不发给自己
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


