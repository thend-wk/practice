package com.thend.home.sweethome.reactor;

import reactor.core.Reactor;
/**
 * an Asynchronous, Event-Driven Application with Reactor
 * @author wangkai
 *
 */
public class Application {
	
	public static void main(String[] args) throws Exception {
		ReactorFactory reactorFactory = new ReactorFactory();
		Reactor reactor = reactorFactory.createReactor();
		//定义发布者
		Publisher publisher = new Publisher(reactor);
		//接受者1
		AbstractReceiver firstReceiver = new FirstReceiver(reactor);
		//接受者2
		AbstractReceiver secondReceiver = new SecondReceiver(reactor);
		
		publisher.publish(AbstractReceiver.KEY_FIRST, new EventModel(1));
		Thread.sleep(1*1000);
		publisher.publish(AbstractReceiver.KEY_SECOND, new EventModel(2));
		Thread.sleep(1*1000);
		publisher.publish(AbstractReceiver.KEY_FIRST, new EventModel(3));

		Thread.sleep(10*1000);
	}

}
