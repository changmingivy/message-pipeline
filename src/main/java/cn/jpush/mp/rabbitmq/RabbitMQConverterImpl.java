package cn.jpush.mp.rabbitmq;

import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by elvin on 16/8/12.
 */
public class RabbitMQConverterImpl implements RabbitMQConverter {


    public byte[] convert(QueueingConsumer.Delivery delivery) {
        return new byte[0];
    }

    public QueueingConsumer.Delivery convert(byte[] data) {
        return null;
    }
}
