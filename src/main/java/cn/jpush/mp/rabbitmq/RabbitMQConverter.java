package cn.jpush.mp.rabbitmq;

import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * Created by elvin on 16/8/12.
 */
public interface RabbitMQConverter {

    byte [] convert(Delivery delivery);

    Delivery convert(byte [] data);

}
