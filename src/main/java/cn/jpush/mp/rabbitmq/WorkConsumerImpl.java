package cn.jpush.mp.rabbitmq;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import cn.jpush.mp.utils.SerializeUtil;

	public class WorkConsumerImpl extends WorkEndPoint implements WorkConsumer,Consumer{

	    private static final Logger Log = LoggerFactory.getLogger(WorkConsumerImpl.class);
	    private String queueName;
	    private Handler handler;
	    private String routingKey;
	    private Config config;

	    public WorkConsumerImpl(Handler handler, String routingKey, String queueName, Config config) throws Exception {
	        super(config);
	        this.config = config;
	        this.handler = handler;
	        this.queueName = queueName;
	        this.routingKey = routingKey;
	    }

	    @Override
	    public void run() {
	        try {
	            channel.queueDeclare(queueName, true, false, false, null);
	            channel.queueBind(queueName, exchangeName, routingKey);
	            channel.basicConsume(queueName, false, this);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	    // 注册事件
	    @Override
	    public void handleConsumeOk(String consumerTag) {
	        Log.info(String.format("Consumer %s registered", consumerTag));
	    }

	    // 收到新消息
	    @Override
	    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
	        Object obj;
	        try {
	            obj = SerializeUtil.getObject(body);
	        } catch (Exception e) {
	            //数据格式异常，不要求重新请求
	            Log.error("MailMessage Error", e);
	            channel.basicNack(envelope.getDeliveryTag(), false, false);
	            return;
	        }

//	        Log.debug(String.format("Receive Push Message : %s", obj));
	        Log.info(
	                String.format("Receive message from server[%s] queue[%s]: %s",
	                        this.config.rabbitMQServer,
	                        this.queueName, obj
	                )
	        );

	        if (handler.handle(obj)) {
	            // send ack
	            channel.basicAck(envelope.getDeliveryTag(), false);
	        } else {
	            // send no ack and ask mq re-queued this message
	            channel.basicNack(envelope.getDeliveryTag(), false, true);
	        }

	    }

	    // 服务停止事件, 重新建立连接
	    @Override
	    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
	        try {
	            Log.error(String.format("Work Consumer:%s Connection Error, Error:%s", consumerTag, sig.getMessage()), sig);
	            Thread.sleep(200);
	            initMQ();
	        } catch (Exception e) {
	            Log.error(String.format("Restart Work Consumer:%s Fail, Error:%s", consumerTag, sig.getMessage()), e);
	        }
	    }

	    @Override
	    public void handleCancelOk(String consumerTag) {
	    }

	    @Override
	    public void handleCancel(String consumerTag) throws IOException {
	    }

	    @Override
	    public void handleRecoverOk(String consumerTag) {
	    }

	    @Override
	    public String getServer(){
	        return config.rabbitMQServer;
	    }

}
