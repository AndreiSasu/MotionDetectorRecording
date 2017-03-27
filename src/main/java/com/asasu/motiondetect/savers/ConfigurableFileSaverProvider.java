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

    @Inject
    private List<IFileSaver> fileSavers;

    final Map<String, IFileSaver> fileSaverCache = new HashMap<>();

    @PostConstruct
    public void initIalizeMaps() {
        fileSavers.forEach(iFileSaver -> fileSaverCache.put(iFileSaver.toString(), iFileSaver));
    }

    @Override
    public IFileSaver getFileSaver() {
        return fileSaverCache.get("google");
    }
}
