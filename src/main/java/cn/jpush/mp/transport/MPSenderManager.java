package cn.jpush.mp.transport;

import cn.jpush.mp.transport.impl.HttpDataSender;
import cn.jpush.mp.transport.impl.LocalDataSender;
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
@Component("senderManager")
public class MPSenderManager {

    private Properties props;

    private Map<String, MPDataSender> sendersMap;

    private MPDataSender localSender = new LocalDataSender();

    public MPSenderManager() throws IOException {
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
            MPDataSender sender = null;

            switch (type) {
                case "http":
                    String address = props.getProperty(senderName + ".address");
                    sender = new HttpDataSender(address);
                    break;
            }
            if (sender != null) {
                sendersMap.put(senderName, sender);
            }
        }
    }

    public void sendData(String senderName, byte [] data) {
        if ("local".equals(senderName)) {
            localSender.sendData(data);
        } else if (sendersMap.get(senderName) != null){
            sendersMap.get(senderName).sendData(data);
        } else {
            throw new RuntimeException("Sender not exists");
        }
    }
}
