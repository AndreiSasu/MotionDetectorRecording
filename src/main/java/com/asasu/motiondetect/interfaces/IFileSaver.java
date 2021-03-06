package com.asasu.motiondetect.interfaces;

import java.io.File;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface IFileSaver extends Runnable, InitializingBean, DisposableBean,
		IConfigurationListener {
	public void save(File f);
}
