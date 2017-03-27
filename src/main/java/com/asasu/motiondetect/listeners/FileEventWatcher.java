package com.asasu.motiondetect.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class FileEventWatcher implements ApplicationContextAware, Runnable {

    private static final Log log = LogFactory.getLog(FileEventWatcher.class);

    private ApplicationContext ctx;

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.ctx = applicationContext;
    }

    public void publish(String message) {
        ctx.publishEvent(new FileSaveEvent(this, message));
    }

    @Override
    public void run() {
        log.debug(FileEventWatcher.class + " running.");
    }
}