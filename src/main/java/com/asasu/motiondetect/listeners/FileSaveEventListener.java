package com.asasu.motiondetect.listeners;

import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import com.asasu.motiondetect.interfaces.IFileSaver;
import com.asasu.motiondetect.interfaces.IFileSaverProvider;

/**
 * Created by andrei.sasu on 3/24/17.
 */
@Component
public class FileSaveEventListener implements ApplicationListener<FileSaveEvent> {

    private static final Log log = LogFactory.getLog(FileSaveEventListener.class);

    private IFileSaverProvider fileSaverProvider;
    private IFileSaver fileSaver;

    @Inject
    public FileSaveEventListener(IFileSaverProvider fileSaverProvider) {
        this.fileSaver = fileSaverProvider.getFileSaver();
    }

    @Override
    public void onApplicationEvent(FileSaveEvent fileSaveEvent) {
        log.debug(fileSaveEvent.getFileName());
        log.debug(fileSaver.toString());
    }
}
