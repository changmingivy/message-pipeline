package cn.jpush.mp.transport;

import cn.jpush.mp.transport.impl.HttpDataSender;
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
public class SenderManager {

    private Properties props;

    private Map<String, DataSender> sendersMap;

    public SenderManager() throws IOException {
        loadConfig();
        initSender();
    }

    private void loadConfig() throws IOException {
        Resource resource = new ClassPathResource("mp-sender.properties");
        props = PropertiesLoaderUtils.loadProperties(resource);
    }

    private void initSender() {
        sendersMap = new HashMap<>();
        String [] sendersSet = props.getProperty("sender.set", "").split(",");

        for (String senderName : sendersSet) {
            String type = props.getProperty(senderName + ".type");
            DataSender sender = null;

            switch (type) {
                case "http":
                    String address = props.getProperty(senderName + ".address");
                    String path = props.getProperty(senderName + ".path");
                    sender = new HttpDataSender(address, path);
                    break;
            }
            if (sender != null) {
                sendersMap.put(senderName, sender);
            }
        }
    }

    public void sendDataToAll(byte [] data) {
        for (DataSender sender : sendersMap.values()) {
            sender.sendData(data);
        }
    }
}
