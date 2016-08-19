package cn.jpush.mp.datatarget;

/**
 * Created by elvin on 16/8/16.
 */
public interface MPProvider {

    void initProvider();

    void shutdown();

    void publishMessage(String routingKey, byte [] body);
}
