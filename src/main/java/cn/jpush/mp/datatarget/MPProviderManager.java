package cn.jpush.mp.datatarget;

import cn.jpush.mp.rabbitmq.RabbitMQConfig;
import cn.jpush.mp.rabbitmq.RabbitMQProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by elvin on 16/8/16.
 */
@Component
public class MPProviderManager {
    private static final Logger logger = LoggerFactory.getLogger(MPProviderManager.class);

    private Properties props;

    private Map<String, Map<String, MPProvider>> providersMap;

    public MPProviderManager() throws IOException {
        loadConfig();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initConsumer();
            }
        }).start();
    }

    private void loadConfig() throws IOException {
        Resource resource = new ClassPathResource("mp-provider.properties");
        props = PropertiesLoaderUtils.loadProperties(resource);
    }

    private void initConsumer() {
        providersMap = new HashMap<>();
        String [] providersSet = props.getProperty("provider.set", "").split(",");

        for (String providers : providersSet) {
            Map<String, MPProvider> providerMap = new HashMap<>();
            String type = props.getProperty(providers + ".type");

            switch (type) {
                case "RabbitMQ":
                    String [] prosSet = props.getProperty(providers + ".set", "").split(",");
                    for (String pros : prosSet) {
                        RabbitMQConfig.Builder rabbitMQConfigBuilder = RabbitMQConfig.createBuilder();
                        rabbitMQConfigBuilder.setServer(props.getProperty(providers + "." + pros + ".server"));
                        rabbitMQConfigBuilder.setPort(Integer.valueOf(props.getProperty(providers + "." + pros + ".port")));
                        rabbitMQConfigBuilder.setUsername(props.getProperty(providers + "." + pros + ".username"));
                        rabbitMQConfigBuilder.setPassword(props.getProperty(providers + "." + pros + ".password"));
                        rabbitMQConfigBuilder.setExchangeName(props.getProperty(providers + "." + pros + ".exchangeName"));
                        rabbitMQConfigBuilder.setExchangeMode(props.getProperty(providers + "." + pros + ".exchangeMode"));
                        rabbitMQConfigBuilder.setQueueName(props.getProperty(providers + "." + pros + ".queueName"));
                        rabbitMQConfigBuilder.setRoutingKey(props.getProperty(providers + "." + pros + ".routingKey"));
                        RabbitMQConfig rabbitConfig = rabbitMQConfigBuilder.build();
                        MPProvider provider = new RabbitMQProviderImpl(rabbitConfig);
                        providerMap.put(pros, provider);
                        provider.initProvider();
                    }
                    break;
            }

            providersMap.put(providers, providerMap);
        }
    }
}