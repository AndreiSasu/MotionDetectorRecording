package com.asasu.motiondetect.interfaces;

import com.asasu.motiondetect.entity.settings.SettingsPolicy;

public interface IConfigurationReloader {
	void reloadConfiguration(SettingsPolicy settingsPolicy);
}
