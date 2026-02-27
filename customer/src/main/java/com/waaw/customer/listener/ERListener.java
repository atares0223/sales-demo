package com.waaw.customer.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ERListener {
    @RabbitListener(queues = "ap.export")
    public void processMessage(String message) {
        System.out.println("Received message: " + message);
    }

}
