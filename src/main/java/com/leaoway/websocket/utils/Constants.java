package com.leaoway.websocket.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.leaoway.websocket.server.WebSocketServer;

public class Constants {

    public static String LOGIN_CHANNEL_NAME = "login";

    public static String LOGOUT_CHANNEL_NAME = "logout";

    public static String SEND_MESSAGE_CHANNEL_NAME = "sendMessage";

    public static ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketServer>> userWebSocketServerSetMap = new ConcurrentHashMap<>();

}
