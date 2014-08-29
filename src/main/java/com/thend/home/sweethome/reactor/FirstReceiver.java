package com.thend.home.sweethome.reactor;

import reactor.core.Reactor;
import reactor.event.Event;

public class FirstReceiver extends AbstractReceiver {
	
	public FirstReceiver(Reactor reactor) {
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
		return KEY_FIRST;
	}
}
