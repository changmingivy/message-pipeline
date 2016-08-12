package cn.jpush.mp.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DealHandler implements Handler {
	private static final Logger log = LoggerFactory.getLogger(DealHandler.class);

	@Override
	public boolean handle(Object obj) {
		log.info("receive msg:"+obj);
		return true;
	}

}
