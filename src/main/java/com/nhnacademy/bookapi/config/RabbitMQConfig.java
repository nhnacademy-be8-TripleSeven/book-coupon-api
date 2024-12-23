package com.nhnacademy.bookapi.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "nhn24.coupon.exchange";
    public static final String QUEUE_NAME = "nhn24.coupon.queue";
    public static final String ROUTING_KEY = "coupon.routing.#";

//    public static final String REPLY_QUEUE_NAME = "coupon.assign.reply.queue"; // 응답 큐 추가

    @Bean
    DirectExchange couponExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue couponQueue() {
        return new Queue(QUEUE_NAME);
    }

//    @Bean
//    public Queue replyQueue() { // 응답 큐 추가
//        return new Queue(REPLY_QUEUE_NAME, true);
//    }

    @Bean
    Binding couponBinding(Queue couponQueue, DirectExchange couponExchange) {
        return BindingBuilder.bind(couponQueue).to(couponExchange).with(ROUTING_KEY);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 응답 메시지를 위한 Queue 설정
//        rabbitTemplate.setReplyAddress(REPLY_QUEUE_NAME);
//        rabbitTemplate.setReplyTimeout(5000); // 응답 대기 시간 설정 (5초)

        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
