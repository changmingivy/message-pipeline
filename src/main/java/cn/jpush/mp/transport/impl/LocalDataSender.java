package cn.jpush.mp.transport.impl;

import cn.jpush.mp.datatarget.MPProviderManager;
import cn.jpush.mp.transport.MPDataSender;
import cn.jpush.mp.utils.SpringContextUtil;

/**
 * Created by elvin on 16/8/16.
 */
public class LocalDataSender implements MPDataSender {

    private final String targetMQ;

    public LocalDataSender(String targetMQ) {
        this.targetMQ = targetMQ;
    }

    @Override
    public void sendData(String routingKey, byte[] data) {
        getMPProviderManager().publishMessage(targetMQ, routingKey, data);
    }

    private MPProviderManager getMPProviderManager() {
        return (MPProviderManager) SpringContextUtil.getBean("providerManager");
    }
}
