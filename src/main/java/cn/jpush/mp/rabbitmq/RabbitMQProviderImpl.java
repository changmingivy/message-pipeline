package cn.jpush.mp.rabbitmq;

import cn.jpush.mp.datatarget.MPProvider;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by elvin on 16/8/16.
 */
public class RabbitMQProviderImpl extends RabbitMQBase implements MPProvider {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProviderImpl.class);

    public RabbitMQProviderImpl(RabbitMQConfig config) {
        super(config);
    }

    @Override
    public void initProvider() {
        try {
            this.shutdown();
            super.init();
        } catch (IOException e) {
            logger.error("启动生产者失败 " + config.server, e);
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
}
