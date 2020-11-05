package com.leaoway.websocket.server;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.leaoway.websocket.utils.Constants;
import com.leaoway.websocket.utils.RedisUtils;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/webSocketServer/{userId}")
@Component
public class WebSocketServer {

    private Session session;

    private String currentUserId;

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws Exception {
        this.session = session;
        currentUserId = userId;
        if (Constants.userWebSocketServerSetMap.get(userId) == null) {
            CopyOnWriteArraySet<WebSocketServer> webSocketServerSet = new CopyOnWriteArraySet<>();
            webSocketServerSet.add(this);
            Constants.userWebSocketServerSetMap.put(userId, webSocketServerSet);

            RedisUtils.send(Constants.LOGIN_CHANNEL_NAME, userId);
        }
        else {
            Constants.userWebSocketServerSetMap.get(userId).add(this);
        }
    }

    @OnClose
    public void onClose() {
        String userId = currentUserId;
        CopyOnWriteArraySet<WebSocketServer> webSocketServerSet = Constants.userWebSocketServerSetMap.get(userId);
        webSocketServerSet.remove(this);
        if (webSocketServerSet.size() == 0) {
            Constants.userWebSocketServerSetMap.remove(userId);
            RedisUtils.send(Constants.LOGOUT_CHANNEL_NAME, userId);
        }
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
