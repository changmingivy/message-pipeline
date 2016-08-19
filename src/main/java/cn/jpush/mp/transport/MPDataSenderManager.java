package cn.jpush.mp.transport;

import cn.jpush.mp.transport.impl.HttpDataSender;
import cn.jpush.mp.transport.impl.LocalDataSender;
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
@Component("senderManager")
public class MPDataSenderManager {

    private Properties props;

    private Map<String, MPDataSender> sendersMap;

    public MPDataSenderManager() throws IOException {
        loadConfig();
        initSender();
    }

    private void loadConfig() throws IOException {
        InputStream inputStream = MPDataSenderManager.class.getResourceAsStream("/mp-sender.properties");
        props = new Properties();
        props.load(inputStream);
//        Resource resource = new ClassPathResource("classpath*:mp-sender.properties");
//        props = PropertiesLoaderUtils.loadProperties(resource);
    }

    private void initSender() {
        sendersMap = new HashMap<>();
        String [] sendersSet = props.getProperty("sender.set", "").split(",");

        for (String senderName : sendersSet) {
            if (StringUtils.isEmpty(senderName)) {
                continue;
            }

            MPDataSender sender = null;
            String type = props.getProperty(senderName + ".type");
            switch (type) {
                case "http":
                    String address = props.getProperty(senderName + ".address");
                    String targetMQ = props.getProperty(senderName + ".targetMQ");
                    sender = new HttpDataSender(address, targetMQ);
                    break;
                case "local":
                    targetMQ = props.getProperty(senderName + ".targetMQ");
                    sender = new LocalDataSender(targetMQ);
                    break;
            }

            if (sender != null) {
                sendersMap.put(senderName, sender);
            }
        }
    }

    public void sendData(String senderName, String routingKey, byte [] data) {
        if (sendersMap.get(senderName) != null){
            sendersMap.get(senderName).sendData(routingKey, data);
        } else {
            throw new RuntimeException("Sender not exists");
        }
    }
}
