package cn.jpush.mp.transport;

/**
 * Created by elvin on 16/8/12.
 */
public interface DataReceiver {

    void handleData(byte [] data);

}
