package com.asasu.motiondetect.interfaces;

import com.asasu.motiondetect.entity.settings.Policy;

public interface IConfigurationReloader {
	public void reloadConfiguration(Policy policy);
}
