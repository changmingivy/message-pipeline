package cn.jpush.mp.rabbitmq;

import cn.jpush.mp.datasource.MPConsumer;
import cn.jpush.mp.transport.MPDataSenderManager;
import cn.jpush.mp.utils.SpringContextUtil;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by elvin on 16/8/19.
 */
public class RabbitMQQueuedConsumerImpl extends RabbitMQBase implements MPConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQQueuedConsumerImpl.class);

    private final String senderName;

    private QueueingConsumer consumer;

    public RabbitMQQueuedConsumerImpl(RabbitMQConfig config, String senderName) {
        super(config);
        this.senderName = senderName;
    }

    @Override
    public void initConsumer() {
        try {
            this.shutdown();
            super.init();

            consumer = new QueueingConsumer(channel);
            channel.basicConsume(config.queueName, false, consumer);
            new Thread(new ConsumerTask()).start();
        } catch (IOException e) {
            logger.error("启动消费者失败 " + config.server, e);
        }
    }

    @Override
    public void shutdown() {
        try {
            super.close();
        } catch (IOException e) {
            logger.error("关闭消费者失败 " + config.server, e);
        }
    }

    public class ConsumerTask implements Runnable  {

        public volatile boolean SHUTDOWN = false;

        @Override
        public void run() {
            while (!SHUTDOWN) {
                try {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    Envelope envelope = delivery.getEnvelope();
                    byte [] body = delivery.getBody();
                    logger.info("{} received message, data length {}, sender {}", config.server, body.length, senderName);
                    try {
                        MPDataSenderManager senderManager = getSenderManager();
                        senderManager.sendData(senderName, envelope.getRoutingKey(), body);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (Exception e) {
                        logger.error(config.server + " message handle failed, sender " + senderName, e);
                        try {
                            channel.basicNack(envelope.getDeliveryTag(), false, true);
                        } catch (IOException e1) {
                            logger.error("Reject RabbitMQ failed", e1);
                        }
                    }
                } catch (InterruptedException e) {
                    logger.error("RabbitMQ异常中断,重新初始化...", e);
                    initConsumer();
                }
            }
        }
    }

    private MPDataSenderManager getSenderManager() {
        return (MPDataSenderManager) SpringContextUtil.getBean("senderManager");
    }
}
