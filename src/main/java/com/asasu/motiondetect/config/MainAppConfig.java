package com.asasu.motiondetect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.asasu.motiondetect.Main;
import com.asasu.motiondetect.entity.settings.SettingsPolicy;

/**
 * Created by andrei.sasu on 3/27/17.
 */
@Configuration
@Import(FileSaverConfig.class)
public class MainAppConfig {

//    @Bean(name = "settingsPolicy")
//    SettingsPolicy policy() {
//        return new SettingsPolicy();
//    }

    @Bean(name = "mainApp")
    Main mainApp() {
        return new Main();
    }
}
