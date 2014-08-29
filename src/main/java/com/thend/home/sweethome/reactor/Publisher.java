package com.thend.home.sweethome.reactor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import reactor.core.Reactor;
import reactor.event.Event;

public class Publisher {
	
	private static final Log logger = LogFactory.getLog(Publisher.class);
	
	private Reactor reactor;
	
	public Publisher(Reactor reactor) {
		this.reactor = reactor;
	}
	
    public void publish(String key, EventModel eventModel) {
        reactor.notify(key, Event.wrap(eventModel));
        logger.info("notify event : " + eventModel.toString() + " with key : " + key);
    }

	public void setReactor(Reactor reactor) {
		this.reactor = reactor;
	}
}
