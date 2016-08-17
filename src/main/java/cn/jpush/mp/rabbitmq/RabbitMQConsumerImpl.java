package cn.jpush.mp.rabbitmq;

import cn.jpush.mp.datasource.MPConsumer;
import cn.jpush.mp.transport.MPDataSenderManager;
import cn.jpush.mp.utils.SpringContextUtil;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created by elvin on 16/8/15.
 */
public class RabbitMQConsumerImpl extends RabbitMQBase implements MPConsumer, Consumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumerImpl.class);

    private final String senderName;

    public RabbitMQConsumerImpl(RabbitMQConfig config, String senderName) {
        super(config);
        this.senderName = senderName;
    }

    @Override
    public void initConsumer() {
        try {
            this.shutdown();
            super.init();
            this.channel.basicConsume(config.queueName, false, this);
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

    @Override
    public void handleConsumeOk(String consumerTag) {

    }

    @Override
    public void handleCancelOk(String consumerTag) {

    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {

    }

    // 服务停止事件, 重新建立连接
    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        try {
            logger.error(String.format("RabbitMQ MPConsumer:%s Connection Error, Error:%s", consumerTag, sig.getMessage()), sig);
            Thread.sleep(200);
            this.initConsumer();
        } catch (Exception e) {
            logger.error(String.format("Restart Work MPConsumer:%s Fail, Error:%s", consumerTag, sig.getMessage()), e);
        }
    }

    @Override
    public void handleRecoverOk(String consumerTag) {

    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        logger.info("{} received message, data length {}, sender {}", config.server, body.length, senderName);
        try {
            MPDataSenderManager senderManager = getSenderManager();
            senderManager.sendData(senderName, body);
            channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (Exception e) {
            logger.error(config.server + " message handle failed, sender " + senderName, e);
            channel.basicNack(envelope.getDeliveryTag(), false, true);
        }
    }

    private MPDataSenderManager getSenderManager() {
        return (MPDataSenderManager) SpringContextUtil.getBean("senderManager");
    }
}
