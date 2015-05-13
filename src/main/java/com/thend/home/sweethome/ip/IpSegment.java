package com.thend.home.sweethome.ip;

import java.text.ParseException;

public class IpSegment implements Comparable<IpSegment> {
	private int from;
	private int to;
	private IpSegmentLocation ipLocation = new IpSegmentLocation();

	public IpSegment() {
	}

	public IpSegment(int from, int to, String description) {
		this.from = from;
		this.to = to;
		this.ipLocation = IpSegmentLocation.parse(description);
	}

	public IpSegment(int from, int to, String country, String province,
			String city, String detail, String com) {
		this.from = from;
		this.to = to;
		this.ipLocation = new IpSegmentLocation(country, province, city, com,
				detail);
	}

	public void setFrom(int value) {
		this.from = value;
	}

	public int getFrom() {
		return this.from;
	}

	public void setTo(int value) {
		this.to = value;
	}

	public int getTo() {
		return this.to;
	}

	void setDescription(String value) {
		this.ipLocation = IpSegmentLocation.parse(value);
	}

	public String getDescription() {
		return ((this.ipLocation == null) ? "" : this.ipLocation.getDetail());
	}

	public void setLocation(IpSegmentLocation location) {
		this.ipLocation = location;
	}

	public IpSegmentLocation getFormatLocation() {
		return this.ipLocation;
	}

	public void copy(IpSegment other) {
		if (other == null)
			return;
		this.from = other.from;
		this.to = other.to;
		if (this.ipLocation == null)
			this.ipLocation = new IpSegmentLocation();
		this.ipLocation.copy(other.ipLocation);
	}

	public static int previousIp(int ip) {
		long i = ip & 0xFFFFFFFF;
		i -= 1L;
		return (int) i;
	}

	public static int nextIp(int ip) {
		long i = ip & 0xFFFFFFFF;
		i += 1L;
		return (int) i;
	}

	public long size() {
		long f = this.from & 0xFFFFFFFF;
		long t = this.to & 0xFFFFFFFF;
		return (t - f + 1L);
	}

	public static int compareIp(int ip1, int ip2) {
		long i1 = ip1 & 0xFFFFFFFF;
		long i2 = ip2 & 0xFFFFFFFF;
		return ((i1 < i2) ? -1 : (i1 > i2) ? 1 : 0);
	}

	public int compareTo(IpSegment o) {
		int fromCmp = compareIp(this.from, o.from);
		if (fromCmp != 0) {
			return fromCmp;
		}
		return compareIp(this.to, o.to);
	}

	public void parse(String line) throws ParseException {
		int start = 0;
		int pos = 0;
		int len = line.length();

		while (pos < len) {
			char ch = line.charAt(pos);
			if ((ch >= '0') && (ch <= '9'))
				break;
			++pos;
		}

		start = pos;
		boolean isAllNum = true;

		while (pos < len) {
			char ch = line.charAt(pos);
			if ((((ch < '0') || (ch > '9'))) && (ch != '.'))
				break;
			++pos;
			if (ch == '.')
				isAllNum = false;
		}
		if ((start >= pos) || (start >= len)) {
			throw new ParseException("bad ip segment : " + line, 0);
		}
		String fromString = line.substring(start, pos);
		if (isAllNum)
			this.from = (int) Long.parseLong(fromString);
		else
			this.from = IPUtil.parseIp(fromString);
		char ch;
		if ((pos < len) && (line.charAt(pos) == '/')) {
			++pos;
			start = pos;
			while (pos < len) {
				ch = line.charAt(pos);
				if (ch < '0')
					break;
				if (ch > '9')
					break;
				++pos;
			}
			if ((start >= pos) || (start >= len)) {
				throw new ParseException("bad ip segment : " + line, 0);
			}
			int mask = (int) Long.parseLong(line.substring(start, pos));
			this.to = (this.from | (1 << 32 - mask) - 1);
		} else {
			while (pos < len) {
				ch = line.charAt(pos);
				if ((ch >= '0') && (ch <= '9'))
					break;
				++pos;
			}
			start = pos;

			while (pos < len) {
				ch = line.charAt(pos);
				if ((((ch < '0') || (ch > '9'))) && (ch != '.'))
					break;
				++pos;
			}
			if ((start >= pos) || (start >= len)) {
				throw new ParseException("bad ip segment : " + line, 0);
			}
			String toString = line.substring(start, pos);
			if (isAllNum)
				this.to = (int) Long.parseLong(fromString);
			else {
				this.to = IPUtil.parseIp(toString);
			}

		}

		while (pos < len) {
			ch = line.charAt(pos);
			if ((ch != ' ') && (ch != '\t'))
				break;
			++pos;
		}
		start = pos;

		for (pos = len - 1; pos >= start; --pos) {
			ch = line.charAt(pos);
			if ((ch != ' ') && (ch != '\t') && (ch != ';') && (ch != '"'))
				break;
		}
		String description = (pos >= start) ? line.substring(start, pos + 1)
				: "";
		this.ipLocation = IpSegmentLocation.parse(description);

		if (compareIp(this.from, this.to) > 0)
			throw new ParseException("from larger than to : " + line, 0);
	}

	public int hashCode() {
		return (this.from + this.to);
	}

	public boolean equals(Object o) {
		if ((o == null) || (o.getClass() != super.getClass()))
			return false;
		IpSegment that = (IpSegment) o;
		return ((this.from == that.from) && (this.to == that.to)
				&& (this.ipLocation != null) && (this.ipLocation
					.equals(that.ipLocation)));
	}

	public String toString() {
		return IPUtil.formatIp(this.from) + "\t" + IPUtil.formatIp(this.to)
				+ "\t" + this.ipLocation.toString();
	}

	public String formatLine() {
		return IPUtil.formatIp(this.from) + "\t" + IPUtil.formatIp(this.to)
				+ "\t" + this.ipLocation.formatLine();
	}
}
