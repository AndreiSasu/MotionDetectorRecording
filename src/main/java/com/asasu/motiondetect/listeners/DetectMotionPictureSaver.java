package com.asasu.motiondetect.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asasu.motiondetect.interfaces.IConfigurationListener;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

public class DetectMotionPictureSaver implements WebcamMotionListener,
IConfigurationListener {
	private static final Log log = LogFactory
			.getLog(DetectMotionPictureSaver.class);
	private Webcam webcam;
	private String outFolder;
	private int maxFramesCaptured = 10;
	private String imageFormat = ImageUtils.FORMAT_JPG;

	public Webcam getWebcam() {
		return webcam;
	}

	public void setWebcam(Webcam webcam) {
		this.webcam = webcam;
	}

	public String getOutFolder() {
		return outFolder;
	}

	public void setOutFolder(String outFolder) {
		this.outFolder = outFolder;
	}

	public int getMaxFramesCaptured() {
		return maxFramesCaptured;
	}

	public String getImageFormat() {
		return imageFormat;
	}

	public DetectMotionPictureSaver(String outFolder) {
		this.outFolder = outFolder;
		this.webcam = Webcam.getDefault();
	}

	public DetectMotionPictureSaver(Webcam webcam, String outFolder) {
		this.outFolder = outFolder;
		this.webcam = webcam;
	}

	public void setMaxFramesCaptured(int maxFramesCaptured) {
		this.maxFramesCaptured = maxFramesCaptured;
	}

	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}

	@Override
	public void motionDetected(WebcamMotionEvent motionEvent) {
		log.debug("Motion detected with area: " + motionEvent.getArea());
		// capture 10 frames
		for (int i = 0; i < maxFramesCaptured; i++) {

			WebcamUtils.capture(
					webcam,
					outFolder
					+ new SimpleDateFormat("yyyy-dd-hh-mm-ss")
					.format(new Date()), imageFormat);
		}
	}

	@Override
	public void onConfigurationChanged() {
		log.debug("************************NEW SETTINGS RECEIVED**************************");

	}
}
