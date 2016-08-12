package cn.jpush.mp.rabbitmq;

public class ConsumerTest {
	public static void main(String[] args) throws Exception {
		Handler handler = new DealHandle();
		Config config = new Config();
		config.rabbitMQServer = "127.0.0.1";
		config.rabbitMQPort ="5672";
		config.rabbitMQUsername="guest";
		config.rabbitMQPassword="guest";
		config.exchangeName="exchange.csy";
		config.basicQos =1;
		config.exchangeMode ="direct";
		
		Thread thread = new Thread(new WorkConsumerLB(handler, "hello", "hello", config));
		thread.start();
		thread.join();
		
	}

}
