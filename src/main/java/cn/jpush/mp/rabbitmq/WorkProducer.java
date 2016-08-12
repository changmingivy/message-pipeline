package cn.jpush.mp.rabbitmq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AlreadyClosedException;

import cn.jpush.mp.utils.SerializeUtil;

public class WorkProducer extends WorkEndPoint {
    private String routingKey;
    private static final Logger Log = LoggerFactory.getLogger(WorkProducer.class);
    private String queueName;

    /**
     * @param routingKey 发送消息的路由关键字，Consumer接收消息需要匹配路由关键字
     * @throws Exception
     */
    public WorkProducer(String routingKey, String queueName, Config config) throws IOException {
        super(config);
        this.routingKey = routingKey;
        this.queueName = queueName;
        super.channel.queueDeclare();
        this.channel.basicQos(config.basicQos);
        //由producer创建供给mailConsumer使用的消息队列，防止消息无队列接收而丢失
        this.channel.queueDeclare(this.queueName, false, false, false, null);
        this.channel.queueBind(queueName, exchangeName, routingKey);
    }

    //发送消息
    public boolean sendMessage(Object message) throws Exception {
        try {
            this.channel.basicPublish(exchangeName, routingKey, null, SerializeUtil.getBytes(message));
            //channel.basicPublish(exchangeName, routingKey, null, getBytes("23333"));
            Log.info("Send message assignment to Consumer success");
            System.out.println("Send message assignment to Consumer success,msg:"+message);
            return true;
        } catch (AlreadyClosedException ace) {
            Log.error(String.format("MQ already closed error,dataToSend=%s", message), ace);
            Thread.sleep(200);
            this.initMQ();
            return false;
        } catch (Exception e) {
            Log.error(String.format("Read mq error,dataToSend=%s", message), e);
            Thread.sleep(200);
            this.initMQ();
            return false;
        }
    }
}
