package com.leaoway.websocket.listener;

import java.util.Iterator;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.leaoway.websocket.server.WebSocketServer;
import com.leaoway.websocket.utils.Constants;
import com.leaoway.websocket.vo.SocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;

public class WebSocketRedisListener implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(WebSocketRedisListener.class);

    @Autowired
    private RedisSerializer<String> stringRedisSerializer;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String channel = stringRedisSerializer.deserialize(message.getChannel());
        String value = stringRedisSerializer.deserialize(message.getBody());
        //登入登出是为了记录当前用户集合
        if (Constants.LOGIN_CHANNEL_NAME.equals(channel)) {
            String user = value;
            logger.info("用户:" + user + "登录");
            logger.info("本机用户集合:" + Constants.userWebSocketServerSetMap.keySet());
        }
        else if (Constants.LOGOUT_CHANNEL_NAME.equals(channel)) {
            String user = value;
            logger.info("用户:" + user + "退出");
            logger.info("本机用户集合:" + Constants.userWebSocketServerSetMap.keySet());
        }
        else if (Constants.SEND_MESSAGE_CHANNEL_NAME.equals(channel)) {
            // 发送消息
            SocketMessage socketMessage = JSON.parseObject(value, SocketMessage.class);
            String userId = socketMessage.getUsrId();
            Set<String> webSocketLoginUserSet = Constants.userWebSocketServerSetMap.keySet();
            if (webSocketLoginUserSet.contains(userId)) {
                Set<WebSocketServer> webSocketServerSet = Constants.userWebSocketServerSetMap.get(userId);
                WebSocketServer latestWebSocketServer = null;
                Iterator<WebSocketServer> iterator = webSocketServerSet.iterator();
                while (iterator.hasNext()) {
                    WebSocketServer webSocketServer = iterator.next();
                    latestWebSocketServer = webSocketServer;
                }
                latestWebSocketServer.sendMessage(socketMessage.getMessage());
            }
        }
    }
}
