package com.asasu.motiondetect.listeners;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class FileEventWatcher implements ApplicationContextAware, Runnable {

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
        this.publish("----");
    }
}