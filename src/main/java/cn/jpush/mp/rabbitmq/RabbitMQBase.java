package cn.jpush.mp.rabbitmq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created by elvin on 16/8/16.
 */
public abstract class RabbitMQBase {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQBase.class);

    protected Connection connection;
    protected Channel channel;

    protected final RabbitMQConfig config;
    protected final Address [] addresses;

    public RabbitMQBase(RabbitMQConfig config) {
        this.config = config;
        addresses = Address.parseAddresses(config.server);
    }

    protected void init() throws IOException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        if (!StringUtils.isEmpty(config.username)) {
            connectionFactory.setUsername(config.username);
        }
        if (!StringUtils.isEmpty(config.password)) {
            connectionFactory.setPassword(config.password);
        }
        this.connection = connectionFactory.newConnection(addresses);
        this.channel = this.connection.createChannel();
        this.channel.basicQos(config.basicQos);
        this.channel.exchangeDeclare(config.exchangeName, config.exchangeMode);
        this.channel.queueDeclare(config.queueName, true, false, false, null);
        this.channel.queueBind(config.queueName, config.exchangeName, config.routingKey);
        logger.info("初始化RabbitMQ成功 {}", config.server);
    }

    protected void close() throws IOException {
        if (channel != null) {
            channel.close();
            channel = null;
        }

        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}
