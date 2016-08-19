package cn.jpush.mp.transport.impl;

import cn.jpush.mp.transport.MPDataSender;

/**
 * Created by elvin on 16/8/15.
 */
public class HttpDataSender implements MPDataSender {

    private final String address;
    private final String targetMQ;

    public HttpDataSender(String address, String targetMQ) {
        this.address = address;
        this.targetMQ = targetMQ;
    }

    @Override
    public void sendData(String routingKey, byte[] data) {
        HttpDataSenderHelper.postByteArray(this.address, data, targetMQ, routingKey);
    }
}
