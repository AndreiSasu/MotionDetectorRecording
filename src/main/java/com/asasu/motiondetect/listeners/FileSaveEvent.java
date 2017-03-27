package com.asasu.motiondetect.listeners;

import org.springframework.context.ApplicationEvent;

/**
 * Created by andrei.sasu on 3/24/17.
 */
public class FileSaveEvent extends ApplicationEvent {

    String filename;

    public FileSaveEvent(Object source, String filename) {
        super(source);
        this.filename = filename;
    }

    public String getFileName() {
        return filename;
    }
}
