package cn.jpush.mp.rabbitmq.test;

import cn.jpush.mp.rabbitmq.Config;
import cn.jpush.mp.rabbitmq.WorkProducer;

public class ProducerTest {
	
	public static void main(String[] args) throws Exception {
		
		Config config = new Config();
		config.rabbitMQServer = "127.0.0.1";
		config.rabbitMQPort ="5672";
		config.rabbitMQUsername="guest";
		config.rabbitMQPassword="guest";
		config.exchangeName="exchange.csy";
		config.basicQos =1;
		config.exchangeMode ="direct";
		
		String msg ="hello world";
		
		new WorkProducer("hello", "hello", config).sendMessage(msg);
	}

}
