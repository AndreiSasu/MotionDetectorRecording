package com.asasu.motiondetect;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.asasu.motiondetect.config.MainAppConfig;
import com.asasu.motiondetect.entity.settings.SettingsPolicy;
import com.asasu.motiondetect.interfaces.IConfigurationListener;
import com.asasu.motiondetect.interfaces.IConfigurationReloader;
import com.asasu.motiondetect.interfaces.IFileSaverProvider;
import com.asasu.motiondetect.listeners.DetectMotionPictureSaver;
import com.asasu.motiondetect.listeners.DetectMotionVideoSaver;
import com.asasu.motiondetect.listeners.FileEventWatcher;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamResolution;
import static com.asasu.motiondetect.constants.Constants.outFolder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Named("mainApp")
@Singleton
public class Main implements Runnable, IConfigurationReloader, InitializingBean, DisposableBean {

	private Webcam webcam;
	private WebcamMotionDetector motionDetector;
	private List<IConfigurationListener> configurationListeners = new ArrayList<>();

    @Inject
    private SettingsPolicy settingsPolicy;

    @Inject
    private FileEventWatcher fileEventWatcher;

	@Inject
	IFileSaverProvider fileSaverProvider;

    private static final Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext mainAppConfigAppContext = new AnnotationConfigApplicationContext(MainAppConfig.class);
        Main main = (Main) mainAppConfigAppContext.getBean("mainApp");
        new Thread(main.fileEventWatcher).start();
		new Thread(main.fileSaverProvider.getFileSaver()).start();
        main.startWebcam(0); // locks webcam
        new Thread(main).start();
	}

	private void startMotionDetection() {
		if (!settingsPolicy.isMotionDetection()) {
			return;
		}
		motionDetector = new WebcamMotionDetector(webcam, settingsPolicy.getPixelThreshold(),
				settingsPolicy.getAreaThreshold(), settingsPolicy.getMotionDetectInterval());

		DetectMotionPictureSaver dmps = new DetectMotionPictureSaver(webcam,
				outFolder);
		DetectMotionVideoSaver dmvs = new DetectMotionVideoSaver(outFolder,
				motionDetector);
		motionDetector.addMotionListener(dmps);
		motionDetector.addMotionListener(dmvs);
		this.registerConfigurationListener(dmps);
		this.registerConfigurationListener(dmvs);
		motionDetector.setInterval(settingsPolicy.getMotionDetectInterval());
		motionDetector.setInertia(settingsPolicy.getMotionInertia());
		motionDetector.start();
	}

	private void stopMotionDetection() {
		this.motionDetector.stop();
	}

	@Override
	public void run() {
		this.startMotionDetection();
		while (true) {
			try {
				Thread.sleep(settingsPolicy.getMotionDetectInterval() * 100);
				this.reloadConfiguration(new SettingsPolicy());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void setWebcam(Webcam webcam) {
		this.webcam = webcam;
	}

	public void registerConfigurationListener(IConfigurationListener il) {
		this.configurationListeners.add(il);
	}

	public void startWebcam(int i) {
		if (Webcam.getWebcams().size() > 1) {
			webcam = Webcam.getWebcams().get(0);
		} else {
			webcam = Webcam.getDefault();
		}
		Dimension size = WebcamResolution.VGA.getSize();
		webcam.setViewSize(size);
		webcam.open();
	}

    public void setPolicy(SettingsPolicy policy) {
        this.settingsPolicy = policy;
    }

	@Override
	public void reloadConfiguration(SettingsPolicy settingsPolicy) {
		// write new settingsPolicy to db
		// wakeup possibly sleeping threads
		// notify listeners
		for (IConfigurationListener cl : configurationListeners) {
			cl.onConfigurationChanged();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		File directory = new File(outFolder);
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

    @Override
    public void destroy() throws Exception {

    }
}
