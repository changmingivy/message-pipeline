package cn.jpush.mp.transport.impl;

import cn.jpush.mp.transport.MPDataSender;

/**
 * Created by elvin on 16/8/15.
 */
public class HttpDataSender implements MPDataSender {

    private final String address;

    public HttpDataSender(String address) {
        this.address = address;
    }

    @Override
    public void sendData(byte[] data) {
        HttpDataSenderHelper.postByteArray(this.address, data);
    }
}
