package cn.jpush.mp.rabbitmq;

import java.io.IOException;

import org.springframework.util.StringUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class WorkEndPoint {
    protected Config config;
    protected final String exchangeName;
    protected Channel channel;
    protected Connection connection;
    protected int prefetchCount = 1;

    public WorkEndPoint(Config config) throws IOException {
        exchangeName = config.exchangeName;
        this.config = config;
        initMQ();
    }

    protected void initMQ() throws IOException {
        this.close();

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.rabbitMQServer);
        if (!StringUtils.isEmpty(config.rabbitMQUsername)) {
            connectionFactory.setUsername(config.rabbitMQUsername);
        }
        if (!StringUtils.isEmpty(config.rabbitMQPassword)) {
            connectionFactory.setPassword(config.rabbitMQPassword);
        }
        if (!StringUtils.isEmpty(config.rabbitMQPort)) {
            connectionFactory.setPort(Integer.parseInt(config.rabbitMQPort));
        }
        this.connection = connectionFactory.newConnection();
        this.channel = connection.createChannel();
        channel.basicQos(prefetchCount);
        channel.exchangeDeclare(exchangeName, config.exchangeMode);
    }

    public void close() {
        try {
            if (this.channel != null) {
                this.channel.close();
            }
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (Exception expect) {
            //DO NOTHING
        }
    }
}
