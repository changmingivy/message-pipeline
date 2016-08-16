package cn.jpush.mp.rabbitmq;

import cn.jpush.mp.datasource.MPConsumer;
import cn.jpush.mp.transport.MPSenderManager;
import cn.jpush.mp.utils.SpringContextUtil;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created by elvin on 16/8/15.
 */
public class RabbitMQConsumerImpl implements MPConsumer, Consumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumerImpl.class);

    private Connection connection;
    private Channel channel;

    private final String senderName;
    private final RabbitMQConfig config;

    public RabbitMQConsumerImpl(RabbitMQConfig config, String senderName) {
        this.config = config;
        this.senderName = senderName;
    }

    @Override
    public void initConsumer() {
        this.shutdown();

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.server);
        if (!StringUtils.isEmpty(config.username)) {
            connectionFactory.setUsername(config.username);
        }
        if (!StringUtils.isEmpty(config.password)) {
            connectionFactory.setPassword(config.password);
        }
        if (config.port != 0) {
            connectionFactory.setPort(config.port);
        }
        try {
            this.connection = connectionFactory.newConnection();
            this.channel = this.connection.createChannel();
            this.channel.basicQos(config.basicQos);
            this.channel.exchangeDeclare(config.exchangeName, config.exchangeMode);
            this.channel.queueDeclare(config.queueName, true, false, false, null);
            this.channel.queueBind(config.queueName, config.exchangeName, config.routingKey);
            this.channel.basicConsume(config.queueName, false, this);
            logger.info("初始化RabbitMQ成功 {}", config.server);
        } catch (IOException e) {
            logger.error("打开RabbitMQ失败 " + config.server, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        try {
            if (channel != null) {
                channel.close();
                channel = null;
            }

            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (IOException e) {
            logger.error("关闭RabbitMQ失败 " + config.server, e);
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
//        getSenderManager().sendData(senderName, body);
    }

    private MPSenderManager getSenderManager() {
        return SpringContextUtil.getBean(MPSenderManager.class);
    }
}
