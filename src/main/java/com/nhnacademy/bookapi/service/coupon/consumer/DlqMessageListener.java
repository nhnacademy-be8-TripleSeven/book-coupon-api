//package com.nhnacademy.bookapi.service.coupon.consumer;
//
//import com.nhnacademy.bookapi.config.RabbitConfig;
//import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class DlqMessageListener {
//
//    @RabbitListener(queues = RabbitConfig.DLQ_NAME)
//    public void handleDlqMessage(CouponAssignRequestDTO message) {
//        log.error("Message received in DLQ: {}", message);
//    }
//}
