package com.asasu.motiondetect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import com.asasu.motiondetect.interfaces.IFileSaverProvider;
import com.asasu.motiondetect.listeners.FileEventWatcher;
import com.asasu.motiondetect.listeners.FileSaveEventListener;
import com.asasu.motiondetect.savers.ConfigurableFileSaverProvider;

/**
 * Created by andrei.sasu on 3/24/17.
 */
@Configuration
@Import(PersistenceConfig.class)
public class FileSaverConfig {

}
