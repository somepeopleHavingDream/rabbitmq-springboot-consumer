package org.yangxin.rabbitmq.rabbitmqspringbootconsumer.rabbitmqspringbootconsumer.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yangxin
 * 1/12/21 1:54 PM
 */
@Slf4j
@Component
public class RabbitReceiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue-01", durable = "true"),
            exchange = @Exchange(value = "exchange-1",
                    type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "springboot.rabbit.*"
    ))
    @RabbitHandler
    public <T> void onMessage(Message<T> message, Channel channel) throws IOException {
//        log.info("消费端：[{}]", message);
        log.info("消费端：[{}]", message.getPayload());

        // assert message.getHeaders() != null
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        // 手工ack
        if (deliveryTag != null) {
            channel.basicAck(deliveryTag, false);
        } else {
            log.error("ack错误，deliverTag: [{}]", deliveryTag);
        }
    }
}
