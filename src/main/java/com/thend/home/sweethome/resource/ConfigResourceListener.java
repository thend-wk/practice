package com.thend.home.sweethome.resource;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigResourceListener extends ResourceListener<String> {
	
	private static final Log logger = LogFactory.getLog(ConfigResourceListener.class);
	
	private String content;

	public ConfigResourceListener(String localFile) {
		super(localFile);
		init();
	}
	
	public ConfigResourceListener(String url, String localFile) {
		super(url, localFile);
		init();
	}

	@Override
	protected String getResource(Object... arg) {
		return content;
	}

	@Override
	protected void updateResource(File file) {
		try {
			System.out.println(FileUtils.readFileToString(file));
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

}
