package cn.jpush.mp.datasource;

import cn.jpush.mp.rabbitmq.RabbitMQConfig;
import cn.jpush.mp.rabbitmq.RabbitMQConsumerImpl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by elvin on 16/8/15.
 */
@Component
public class MPConsumerManager {

    private Properties props;

    private Map<String, Map<String, MPConsumer>> consumersMap;

    public MPConsumerManager() throws IOException {
        loadConfig();
        initConsumer();
    }

    private void loadConfig() throws IOException {
        Resource resource = new ClassPathResource("mp-consumer.properties");
        props = PropertiesLoaderUtils.loadProperties(resource);
    }

    private void initConsumer() {
        consumersMap = new HashMap<>();
        String [] consumersSet = props.getProperty("consumer.set", "").split(",");

        for (String consumers : consumersSet) {
            Map<String, MPConsumer> consumerMap = new HashMap<>();
            String type = props.getProperty(consumers + ".type");

            switch (type) {
                case "RabbitMQ":
                    String [] consSet = props.getProperty(consumers + ".set", "").split(",");
                    for (String cons : consSet) {
                        RabbitMQConfig.Builder rabbitMQConfigBuilder = RabbitMQConfig.createBuilder();
                        rabbitMQConfigBuilder.setServer(props.getProperty(consumers + "." + cons + ".server"));
                        rabbitMQConfigBuilder.setPort(Integer.valueOf(props.getProperty(consumers + "." + cons + ".port")));
                        rabbitMQConfigBuilder.setUsername(props.getProperty(consumers + "." + cons + ".username"));
                        rabbitMQConfigBuilder.setPassword(props.getProperty(consumers + "." + cons + ".password"));
                        rabbitMQConfigBuilder.setExchangeName(props.getProperty(consumers + "." + cons + ".exchangeName"));
                        rabbitMQConfigBuilder.setExchangeMode(props.getProperty(consumers + "." + cons + ".exchangeMode"));
                        rabbitMQConfigBuilder.setQueueName(props.getProperty(consumers + "." + cons + ".queueName"));
                        rabbitMQConfigBuilder.setRoutingKey(props.getProperty(consumers + "." + cons + ".routingKey"));
                        RabbitMQConfig rabbitConfig = rabbitMQConfigBuilder.build();
                        MPConsumer consumer = new RabbitMQConsumerImpl(rabbitConfig);
                        consumerMap.put(cons, consumer);
                        consumer.initConsumer();
                    }
                    break;
            }

            consumersMap.put(consumers, consumerMap);
        }
    }

}
