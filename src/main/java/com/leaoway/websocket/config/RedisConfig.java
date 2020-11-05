package com.leaoway.websocket.config;

import com.leaoway.websocket.listener.WebSocketRedisListener;
import com.leaoway.websocket.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory,
            RedisSerializer<String> stringRedisSerializer) {
        RedisTemplate redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        return redisTemplate;
    }

    @Bean
    RedisSerializer<String> stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    MessageListener webSocketRedisListener() {
        return new WebSocketRedisListener();
    }

    @Bean
    MessageListenerAdapter webSocketRedisListenerAdapter(MessageListener webSocketRedisListener) {
        return new MessageListenerAdapter(webSocketRedisListener);
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisTemplate redisTemplate,
            MessageListenerAdapter webSocketRedisListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(webSocketRedisListenerAdapter, new PatternTopic(Constants.LOGIN_CHANNEL_NAME));
        container.addMessageListener(webSocketRedisListenerAdapter, new PatternTopic(Constants.LOGOUT_CHANNEL_NAME));
        container.addMessageListener(webSocketRedisListenerAdapter,
                new PatternTopic(Constants.SEND_MESSAGE_CHANNEL_NAME));
        return container;
    }
}
