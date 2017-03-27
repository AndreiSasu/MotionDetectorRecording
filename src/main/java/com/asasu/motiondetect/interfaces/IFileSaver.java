package com.asasu.motiondetect.interfaces;

import java.io.File;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface IFileSaver extends Runnable, IConfigurationListener, InitializingBean, DisposableBean {
	void save(File f);
}
