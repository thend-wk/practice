package com.thend.home.sweethome.reactor;

import com.thend.home.sweethome.redis.Serializer;

public class EventModel {
	
	private int id;
	
	public EventModel(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return Serializer.toJson(this, false);
	}

}
