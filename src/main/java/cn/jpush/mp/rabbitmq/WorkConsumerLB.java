package cn.jpush.mp.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkConsumerLB implements Runnable{

    private static final Logger Log = LoggerFactory.getLogger(WorkConsumerLB.class);
    private WorkConsumer[] workConsumers;

    public WorkConsumerLB(Handler handler, String routingKey, String queueName, Config config) throws Exception {
        String []rabbitMQServers = config.rabbitMQServer.split(",");
        workConsumers = new WorkConsumer[rabbitMQServers.length];
        String []ports = config.rabbitMQPort.split(",");
        String []usernames = config.rabbitMQUsername.split(",");
        String []passwords = config.rabbitMQPassword.split(",");

        for(int i = 0; i < workConsumers.length; ++ i){
            Config cf = new Config();
            cf.rabbitMQServer = rabbitMQServers[i];
            cf.rabbitMQPort = ports[i];
            cf.rabbitMQUsername = usernames[i];
            cf.rabbitMQPassword = passwords[i];
            cf.exchangeName = config.exchangeName;
            cf.exchangeMode = config.exchangeMode;
            cf.basicQos = config.basicQos;
            workConsumers[i] = new WorkConsumerImpl(handler, routingKey, queueName, cf);
        }
    }

    @Override
    public void run() {
        for(int i = 0; i < workConsumers.length; ++ i){
            try{
                workConsumers[i].run();
            } catch (Exception ex){
                Log.error(String.format("consumer for %s start up fail", workConsumers[i].getServer()));
            }
        }
    }

}
