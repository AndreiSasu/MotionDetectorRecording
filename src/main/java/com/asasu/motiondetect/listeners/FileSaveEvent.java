package com.asasu.motiondetect.listeners;

import org.springframework.context.ApplicationEvent;

/**
 * Created by andrei.sasu on 3/24/17.
 */
public class FileSaveEvent extends ApplicationEvent {
    public FileSaveEvent(Object source) {
        super(source);
    }
}
