package com.nhnacademy.bookapi.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "coupon.exchange";
    public static final String QUEUE_NAME = "coupon.assign.queue";
    public static final String ROUTING_KEY = "coupon.assign";

    public static final String REPLY_QUEUE_NAME = "coupon.assign.reply.queue"; // 응답 큐 추가

    @Bean
    public TopicExchange couponExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue couponQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue replyQueue() { // 응답 큐 추가
        return new Queue(REPLY_QUEUE_NAME, true);
    }

    @Bean
    public Binding couponBinding(Queue couponQueue, TopicExchange couponExchange) {
        return BindingBuilder.bind(couponQueue).to(couponExchange).with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 응답 메시지를 위한 Queue 설정
        rabbitTemplate.setReplyAddress(REPLY_QUEUE_NAME);
        rabbitTemplate.setReplyTimeout(5000); // 응답 대기 시간 설정 (5초)

        return rabbitTemplate;
    }
}
