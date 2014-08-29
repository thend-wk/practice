package com.thend.home.sweethome.reactor;

import reactor.core.Reactor;
import reactor.event.Event;

public class SecondReceiver extends AbstractReceiver {
	
	public SecondReceiver(Reactor reactor) {
		this.reactor = reactor;
		init();
	}

	public void accept(Event<EventModel> event) {
		logger.info("receive event : " + event.getData().toString());
	}

	@Override
	protected String getLogClazz() {
		return this.getClass().getName();
	}

	@Override
	protected String getReactorKey() {
		return KEY_SECOND;
	}

}
