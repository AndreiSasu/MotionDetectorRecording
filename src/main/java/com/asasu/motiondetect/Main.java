package com.asasu.motiondetect;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.asasu.motiondetect.config.AppConfig;
import com.asasu.motiondetect.entity.settings.Policy;
import com.asasu.motiondetect.interfaces.IConfigurationListener;
import com.asasu.motiondetect.interfaces.IConfigurationReloader;
import com.asasu.motiondetect.interfaces.IFileSaver;
import com.asasu.motiondetect.listeners.DetectMotionPictureSaver;
import com.asasu.motiondetect.listeners.DetectMotionVideoSaver;
import com.asasu.motiondetect.listeners.FileEventWatcher;
import com.asasu.motiondetect.services.SettingsService;
import com.asasu.motiondetect.services.UpdateService;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.github.sarxos.webcam.WebcamResolution;
import static com.asasu.motiondetect.constants.Constants.outFolder;

public class Main implements Runnable, IConfigurationReloader, InitializingBean {

	private Webcam webcam;
	private int inertia; // how long motion is valid
	private int pixelThreshold;
	private double areaThreshold;
	private int interval;
	private boolean motionDetection;
	private WebcamMotionDetector motionDetector;
	private List<IFileSaver> fileSavers;
	private List<IConfigurationListener> configurationListeners = new ArrayList<>();
	private List<WebcamMotionListener> motionListeners = new ArrayList<>();

	public static void main(String[] args) throws InterruptedException {
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
		ctx.load("classpath:app-context.xml");
		ctx.refresh();
		Main mainApp = (Main) ctx.getBean("mainApp");
		mainApp.startWebcam(0); // locks webcam
		SettingsService ss = new SettingsService();
		ss.setReloadListener(mainApp);
		UpdateService us = new UpdateService();

		new Thread(ss).start();
		new Thread(us).start();
		for (IFileSaver fs : mainApp.fileSavers) {
			new Thread(fs).start();
			mainApp.registerConfigurationListener(fs);
		}
		new Thread(mainApp).start();

//		ApplicationContext appConfigAppContext = new AnnotationConfigApplicationContext(AppConfig.class);
//		FileEventWatcher fileEventWatcher = (FileEventWatcher)appConfigAppContext.getBean("fileEventWatcher");
//		fileEventWatcher.publish("------");
	}

	private void startMotionDetection() {
		if (!motionDetection) {
			return;
		}
		motionDetector = new WebcamMotionDetector(webcam, pixelThreshold,
				areaThreshold, interval);

		DetectMotionPictureSaver dmps = new DetectMotionPictureSaver(webcam,
				outFolder);
		DetectMotionVideoSaver dmvs = new DetectMotionVideoSaver(outFolder,
				motionDetector);
		motionDetector.addMotionListener(dmps);
		motionDetector.addMotionListener(dmvs);
		this.registerConfigurationListener(dmps);
		this.registerConfigurationListener(dmvs);
		motionDetector.setInterval(interval);
		motionDetector.setInertia(inertia);
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
				Thread.sleep(interval * 100);
				this.reloadConfiguration(new Policy());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void setWebcam(Webcam webcam) {
		this.webcam = webcam;
	}

	public void setInertia(int inertia) {
		this.inertia = inertia;
	}

	public void setPixelThreshold(int pixelThreshold) {
		this.pixelThreshold = pixelThreshold;
	}

	public void setAreaThreshold(double areaThreshold) {
		this.areaThreshold = areaThreshold;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setMotionDetection(boolean motionDetection) {
		this.motionDetection = motionDetection;
	}

	public void setFileSavers(List<IFileSaver> fileSavers) {
		this.fileSavers = fileSavers;
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

	@Override
	public void reloadConfiguration(Policy policy) {
		// write new policy to db
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
}
