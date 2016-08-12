package cn.jpush.mp.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DealHandle implements Handler {
	private static final Logger log = LoggerFactory.getLogger(DealHandle.class);

	@Override
	public boolean handle(Object obj) {
		log.info("receive msg:"+obj);
		System.out.println(obj);
		return true;
	}

}
