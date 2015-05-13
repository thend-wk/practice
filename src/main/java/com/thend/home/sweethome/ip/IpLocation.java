package com.thend.home.sweethome.ip;

import java.io.File;
import java.text.ParseException;
import java.util.List;

public class IpLocation {

	private IpSegments ipSegments = new IpSegments();

	public IpLocation(String file) {
		load(file);
	}

	public void load(String file) {
		try {
			ipSegments.getSegments().clear();
			ipSegments.load(new File(file), true, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public List<IpSegment> getIpSegments() {
		return this.ipSegments.getSegments();
	}

	public IpSegment getLocate(int ip) {
		return this.ipSegments.locate(ip);
	}

	public IpSegment getLocate(String ip_str) {
		try {
			return this.ipSegments.locate(IPUtil.parseIp(ip_str));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
