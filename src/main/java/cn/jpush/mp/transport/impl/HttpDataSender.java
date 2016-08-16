package cn.jpush.mp.transport.impl;

import cn.jpush.mp.transport.MPDataSender;

/**
 * Created by elvin on 16/8/15.
 */
public class HttpDataSender implements MPDataSender {

    private final String address;
    private final String path;

    public HttpDataSender(String address, String path) {
        this.address = address;
        this.path = path;
    }

    @Override
    public void sendData(byte[] data) {
        HttpDataSenderHelper.postByteArray(this.address + this.path, data);
    }
}
