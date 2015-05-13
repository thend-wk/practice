package com.thend.home.sweethome.ip;

public class IpSegmentLocation {
	private String country = null;
	private String province = null;
	private String city = null;
	private String com = null;
	private String detail = null;

	public IpSegmentLocation() {
	}

	public IpSegmentLocation(String country, String province, String city,
			String com, String detail) {
		this.country = country;
		this.province = province;
		this.city = city;
		this.com = com;
		this.detail = detail;
	}

	public void copy(IpSegmentLocation location) {
		this.city = location.city;
		this.com = location.com;
		this.country = location.country;
		this.province = location.province;
		this.detail = location.detail;
	}

	public boolean equals(IpSegmentLocation location) {
		return ((getCity().equals(location.getCity()))
				&& (getProvince().equals(location.getProvince()))
				&& (getCountry().equals(location.getCountry()))
				&& (getCom().equals(location.getCom())) && (getDetail()
					.equals(location.getDetail())));
	}

	public void setCountry(String country) {
		this.country = ((country == null) ? "中国" : country);
	}

	public String getCountry() {
		this.country = ((this.country == null) ? "中国" : this.country);
		return this.country;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvince() {
		return ((this.province == null) ? "" : this.province);
	}

	public String getProvinceWithMoreInfo() {
		if ((this.province == null) || (this.province.length() <= 1)) {
			return getCountry();
		}
		return this.province;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return ((this.city == null) ? "" : this.city);
	}

	public String getCityWithMoreInfo() {
		if ((this.city == null) || (this.city.length() <= 1)) {
			return getProvinceWithMoreInfo();
		}
		return getCity();
	}

	public String getProvinceCity() {
		return getProvince() + getCity();
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getCom() {
		return (((this.com == null) || (this.com.length() <= 1)) ? "不能识别"
				: this.com);
	}

	public void setDetail(String detail) {
		this.detail = detail.replace(";", " ");
	}

	public String getDetail() {
		if ((this.detail == null) || (this.detail.length() <= 1)) {
			String country = ((this.country == null) || (this.country
					.equals("中国"))) ? "" : this.country;

			String province = (this.province == null) ? "" : this.province;
			String city = (this.city == null) ? "" : this.city;
			String com = (this.com == null) ? "" : this.com;
			return country + province + city + " " + com;
		}
		return this.detail;
	}

	public String toString() {
		return getDetail();
	}

	public String formatLine() {
		String country = getCountry();
		String province = getProvince();
		String city = getCity();
		String com = getCom();
		return country + ";" + province + ";" + city + ";" + com + ";"
				+ this.detail;
	}

	public static IpSegmentLocation parse(String s) {
		if ((s == null) || (s.length() == 0))
			return null;

		int pos = 0;
		int len = s.length();
		while (pos < len) {
			char c = s.charAt(pos);
			if ((c != ' ') && (c != '.') && (((c < '0') || (c > '9')))
					&& (c != ';'))
				break;
			++pos;
		}
		String info = s.substring(pos);
		IpSegmentLocation seg = new IpSegmentLocation();
		String[] split = info.split(";");
		if (split.length < 5) {
			seg.setDetail(info);
			return seg;
		}

		seg.setCountry(split[0]);
		seg.setProvince(split[1]);
		seg.setCity(split[2]);
		seg.setCom(split[3]);
		seg.setDetail(split[4]);
		return seg;
	}
}
