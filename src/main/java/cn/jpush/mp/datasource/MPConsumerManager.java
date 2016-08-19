package cn.jpush.mp.datasource;

import cn.jpush.mp.rabbitmq.RabbitMQConfig;
import cn.jpush.mp.rabbitmq.RabbitMQConsumerImpl;
import cn.jpush.mp.rabbitmq.RabbitMQQueuedConsumerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by elvin on 16/8/15.
 */
@Component
public class MPConsumerManager {
    private static final Logger logger = LoggerFactory.getLogger(MPConsumerManager.class);

    private Properties props;

    private Map<String, Map<String, MPConsumer>> consumersMap;

    public MPConsumerManager() throws IOException {
        loadConfig();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    logger.error("延迟调用失败", e);
                }
                initConsumer();
            }
        }).start();
    }

    private void loadConfig() throws IOException {
        InputStream inputStream = MPConsumerManager.class.getResourceAsStream("/mp-consumer.properties");
        props = new Properties();
        props.load(inputStream);
//        Resource resource = new ClassPathResource("classpath*:mp-consumer.properties");
//        props = PropertiesLoaderUtils.loadProperties(resource);
    }

    private void initConsumer() {
        consumersMap = new HashMap<>();
        String [] consumersSet = props.getProperty("consumer.set", "").split(",");

        for (String consumers : consumersSet) {
            if (StringUtils.isEmpty(consumers)) {
                continue;
            }
            Map<String, MPConsumer> consumerMap = new HashMap<>();
            String type = props.getProperty(consumers + ".type");

            switch (type) {
                case "RabbitMQ":
                    String [] consSet = props.getProperty(consumers + ".set", "").split(",");
                    for (String cons : consSet) {
                        RabbitMQConfig.Builder rabbitMQConfigBuilder = RabbitMQConfig.createBuilder();
                        rabbitMQConfigBuilder.setServer(props.getProperty(consumers + "." + cons + ".server"));
                        rabbitMQConfigBuilder.setUsername(props.getProperty(consumers + "." + cons + ".username"));
                        rabbitMQConfigBuilder.setPassword(props.getProperty(consumers + "." + cons + ".password"));
                        rabbitMQConfigBuilder.setExchangeName(props.getProperty(consumers + "." + cons + ".exchangeName"));
                        rabbitMQConfigBuilder.setExchangeMode(props.getProperty(consumers + "." + cons + ".exchangeMode"));
                        rabbitMQConfigBuilder.setQueueName(props.getProperty(consumers + "." + cons + ".queueName"));
                        rabbitMQConfigBuilder.setRoutingKey(props.getProperty(consumers + "." + cons + ".routingKey"));
                        rabbitMQConfigBuilder.setBasicQos(Integer.valueOf(props.getProperty(consumers + "." + cons + ".basicQos")));
                        RabbitMQConfig rabbitConfig = rabbitMQConfigBuilder.build();
                        String senderName = props.getProperty(consumers + "." + cons + ".sender", "");
                        MPConsumer consumer = new RabbitMQConsumerImpl(rabbitConfig, senderName);
                        consumerMap.put(cons, consumer);
                        consumer.initConsumer();
                    }
                    break;
                case "RabbitMQQueued":
                    consSet = props.getProperty(consumers + ".set", "").split(",");
                    for (String cons : consSet) {
                        RabbitMQConfig.Builder rabbitMQConfigBuilder = RabbitMQConfig.createBuilder();
                        rabbitMQConfigBuilder.setServer(props.getProperty(consumers + "." + cons + ".server"));
                        rabbitMQConfigBuilder.setUsername(props.getProperty(consumers + "." + cons + ".username"));
                        rabbitMQConfigBuilder.setPassword(props.getProperty(consumers + "." + cons + ".password"));
                        rabbitMQConfigBuilder.setExchangeName(props.getProperty(consumers + "." + cons + ".exchangeName"));
                        rabbitMQConfigBuilder.setExchangeMode(props.getProperty(consumers + "." + cons + ".exchangeMode"));
                        rabbitMQConfigBuilder.setQueueName(props.getProperty(consumers + "." + cons + ".queueName"));
                        rabbitMQConfigBuilder.setRoutingKey(props.getProperty(consumers + "." + cons + ".routingKey"));
                        rabbitMQConfigBuilder.setBasicQos(Integer.valueOf(props.getProperty(consumers + "." + cons + ".basicQos")));
                        RabbitMQConfig rabbitConfig = rabbitMQConfigBuilder.build();
                        String senderName = props.getProperty(consumers + "." + cons + ".sender", "");
                        MPConsumer consumer = new RabbitMQQueuedConsumerImpl(rabbitConfig, senderName);
                        consumerMap.put(cons, consumer);
                        consumer.initConsumer();
                    }
                    break;
            }

            consumersMap.put(consumers, consumerMap);
        }
    }

}
