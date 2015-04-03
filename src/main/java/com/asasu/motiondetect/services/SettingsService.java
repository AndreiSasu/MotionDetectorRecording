package com.asasu.motiondetect.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asasu.motiondetect.interfaces.IConfigurationReloader;
import com.asasu.motiondetect.savers.FileSearch;

public class SettingsService implements Runnable {
	private static final Log log = LogFactory.getLog(FileSearch.class);

	public IConfigurationReloader reloadListener;

	public void setReloadListener(IConfigurationReloader reloadListener) {
		this.reloadListener = reloadListener;
	}

	private void startService() {
		log.info("Started settings service with listener: "
				+ this.reloadListener.getClass());
	}

	@Override
	public void run() {
		this.startService();
	}
}
