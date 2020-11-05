package com.leaoway.websocket.controller;

import com.alibaba.fastjson.JSON;
import com.leaoway.websocket.utils.Constants;
import com.leaoway.websocket.utils.RedisUtils;
import com.leaoway.websocket.vo.SocketMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebsocketController {

    @GetMapping("/testWebsocket/{userId}/{message}")
    public String testWebsocket(@PathVariable("userId") String userId, @PathVariable("message") String message) {
        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setUsrId(userId);
        socketMessage.setMessage(message);
        RedisUtils.send(Constants.SEND_MESSAGE_CHANNEL_NAME, JSON.toJSONString(socketMessage));
        return "success";
    }
}
