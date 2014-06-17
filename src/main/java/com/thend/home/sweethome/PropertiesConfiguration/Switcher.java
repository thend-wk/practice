package com.thend.home.sweethome.PropertiesConfiguration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Switcher {
	
	private static final Log logger = LogFactory.getLog(Switcher.class);
	
	private static final PropertiesConfiguration conf;
	
	public static final String CONF_AAA_KEY = "aaa";
	public static final String CONF_BBB_KEY = "bbb";
	public static final int RELOAD_DELAY = 60*1000;
	
	static {
		try {
			conf = new PropertiesConfiguration(Switcher.class.getClassLoader().getResource("conf.properties"));
	        /*
	         * Reload the configuration file when the underlying file is modified.
	         * The reloading operation is performed when getting values.
	         */
	        FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
	        reloadingStrategy.setRefreshDelay(RELOAD_DELAY);
	        conf.setReloadingStrategy(reloadingStrategy);
		} catch (ConfigurationException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}
	
    public static int getValue(String key, int defaultValue) {
        return conf.getInt(key, defaultValue);
    }
    
    public static long getValue(String key, long defaultValue) {
        return conf.getLong(key, defaultValue);
    }
    
    public static String getValue(String key, String defaultValue) {
        return conf.getString(key, defaultValue);
    }

}
