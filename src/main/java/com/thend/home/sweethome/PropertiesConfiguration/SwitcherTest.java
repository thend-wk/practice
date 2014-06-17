package com.thend.home.sweethome.PropertiesConfiguration;

public class SwitcherTest {
	
	public static void main(String[] args) {
		for(int i = 0;i<100;i++) {
			System.out.println(Switcher.getValue(Switcher.CONF_AAA_KEY, 0));
			System.out.println(Switcher.getValue(Switcher.CONF_BBB_KEY, ""));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
