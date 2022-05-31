package org.yangxin.rabbitmq.rabbitmqspringbootconsumer.rabbitmqspringbootconsumer.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.yangxin.rabbitmq.rabbitmqspringbootconsumer.rabbitmqspringbootconsumer.entity.Order;

import java.io.IOException;
import java.util.Map;

/**
 * @author yangxin
 * 1/12/21 1:54 PM
 */
@SuppressWarnings("AlibabaRemoveCommentedCode")
@Slf4j
@Component
public class RabbitReceiver {

    /*
        - 消费端可靠性投递
            - ack机制改为手动（RabbitMQ的自动ack机制默认在消息发出后就立即将这条消息删除，而不管消费端是否接收到，是否处理完）
            - SpringBoot提供的消息重试
     */

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

    /**
     * 此案例是失败的！
     * 因为要必须保证这个Order的类全限定名和生产者的类全限定名相同，否则会序列化失败。
     * 建议生产者将数据以json格式入队，再由消费者将json数据反序列化。
     */
    @Deprecated
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${spring.rabbitmq.listener.order.key}"
    ))
    @RabbitHandler
    public void onOrderMessage(@Payload Order order, Channel channel, @Headers Map<String, Object> headerMap) throws IOException {
        log.info("消费端order: [{}]", order.getId());

        Long deliveryTag = (Long) headerMap.get(AmqpHeaders.DELIVERY_TAG);
        // 手工ack
        channel.basicAck(deliveryTag, false);
    }
}
