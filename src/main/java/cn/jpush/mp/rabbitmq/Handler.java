package cn.jpush.mp.rabbitmq;

public interface Handler {
	public boolean handle(Object obj);
}
