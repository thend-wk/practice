package com.thend.home.sweethome.reactor;
import static reactor.event.selector.Selectors.$;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

public abstract class AbstractReceiver implements Consumer<Event<EventModel>> {
	
	protected final Log logger = LogFactory.getLog(getLogClazz());
	
	public static final String KEY_FIRST = "first";
	public static final String KEY_SECOND = "second";
	
	protected Reactor reactor;
	
	protected void init() {
		reactor.on($(getReactorKey()), this);
	}
	
	public void setReactor(Reactor reactor) {
		this.reactor = reactor;
	}
	
	protected abstract String getLogClazz();
	protected abstract String getReactorKey();
}
