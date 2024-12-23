//package com.nhnacademy.bookapi.config;
//
//import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.DefaultClassMapper;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableRabbit
//public class RabbitConfig {
//    public static final String EXCHANGE_NAME = "coupon.exchange";
//    public static final String QUEUE_NAME = "coupon.assign.queue";
//    public static final String ROUTING_KEY = "coupon.assign";
//
//    @Bean
//    public DirectExchange exchange() {
//        return new DirectExchange(EXCHANGE_NAME);
//    }
//
//    @Bean
//    public Queue queue() {
//        return new Queue(QUEUE_NAME, true);
//    }
//
//    @Bean
//    public Binding binding(Queue queue, DirectExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
//    }
//
//    @Bean
//    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
//        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
//        DefaultClassMapper classMapper = new DefaultClassMapper();
//        Map<String, Class<?>> idClassMapping = new HashMap<>();
//        idClassMapping.put("com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO", CouponAssignRequestDTO.class);
//        classMapper.setIdClassMapping(idClassMapping);
//        converter.setClassMapper(classMapper);
//        return converter;
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
//        return rabbitTemplate;
//    }
//
//
//}
package com.nhnacademy.bookapi.config;

import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "coupon.exchange";
    public static final String QUEUE_NAME = "coupon.assign.queue";
    public static final String DLQ_NAME = "coupon.assign.queue.dlq";
    public static final String ROUTING_KEY = "coupon.assign";
    public static final String DLQ_ROUTING_KEY = "coupon.assign.dlq";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_NAME); // DLQ용 교환기 설정
        args.put("x-dead-letter-routing-key", DLQ_ROUTING_KEY); // DLQ 라우팅 키 설정
        return new Queue(QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public Queue dlq() {
        return new Queue(DLQ_NAME, true);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, DirectExchange exchange) {
        return BindingBuilder.bind(dlq).to(exchange).with(DLQ_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO", CouponAssignRequestDTO.class);
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
