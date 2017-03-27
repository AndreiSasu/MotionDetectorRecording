package com.asasu.motiondetect.savers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.asasu.motiondetect.entity.settings.SettingsPolicy;
import com.asasu.motiondetect.interfaces.IFileSaver;
import com.asasu.motiondetect.interfaces.IFileSaverProvider;

/**
 * Created by andrei.sasu on 3/27/17.
 */

@Service
public class ConfigurableFileSaverProvider implements IFileSaverProvider {


    @Autowired
    Environment env;

    IFileSaver fileSaver;

    @Inject
    SettingsPolicy settingsPolicy;

    @Inject
    private List<IFileSaver> fileSavers;


    final Map<String, IFileSaver> fileSaverCache = new HashMap<>();

    public ConfigurableFileSaverProvider() {
//        fileSavers.forEach(fileSaver2 -> {
//            fileSaverCache.put(fileSaver2.getName(), fileSaver2);
//        });
////        IFileSaver fileSaver1 = fileSaverCache.get(fileSaver);
////        this.fileSaver = fileSaver1;
    }

    @PostConstruct
    public void initIalizeMaps() {
        getFileSaver();
    }

    @Override
    public IFileSaver getFileSaver() {
        return new GoogleDriveSaver();
    }
}
