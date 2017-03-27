package com.asasu.motiondetect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import com.asasu.motiondetect.listeners.FileEventWatcher;
import com.asasu.motiondetect.listeners.FileSaveEventListener;

/**
 * Created by andrei.sasu on 3/24/17.
 */
@Configuration
@ComponentScan(basePackages = {"com.asasu.motiondetect"})
public class AppConfig {
    @Autowired
    Environment env;

    @Autowired
    FileSaveEventListener fileSaveEventListener;

    @Bean(name = "fileEventWatcher")
    FileEventWatcher fileEventWatcher() {
        return new FileEventWatcher();
    }

    @Bean(name = "fileSaveEventListener")
    FileSaveEventListener fileSaveEventListener() {
        return new FileSaveEventListener();
    }
}
