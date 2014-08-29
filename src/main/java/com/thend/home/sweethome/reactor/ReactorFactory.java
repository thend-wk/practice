package com.thend.home.sweethome.reactor;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;

public class ReactorFactory {
	
	public Reactor createReactor() {
		Reactor reactor = Reactors.reactor()
					.env(new Environment())
					.dispatcher(Environment.THREAD_POOL)
					.get();
		return reactor;
	}
}
