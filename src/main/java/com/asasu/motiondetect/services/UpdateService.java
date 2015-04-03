package com.asasu.motiondetect.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asasu.motiondetect.savers.FileSearch;

public class UpdateService implements Runnable {
	private static final Log log = LogFactory.getLog(FileSearch.class);

	public boolean hasUpdates() {
		return false;
	}

	private void checkForUpdates() {
		if (this.hasUpdates()) {
			this.update();
		} else {
			log.info("No updates at this time.");
		}
	}

	public void update() {

	}

	@Override
	public void run() {
		log.debug("Started Update Service");
		while (true) {
			this.checkForUpdates();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
