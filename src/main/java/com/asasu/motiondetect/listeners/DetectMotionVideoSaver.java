package com.asasu.motiondetect.listeners;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asasu.motiondetect.interfaces.IConfigurationListener;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class DetectMotionVideoSaver implements WebcamMotionListener,
		IConfigurationListener {
	private static final Log log = LogFactory
			.getLog(DetectMotionVideoSaver.class);

	private Webcam webcam;
	private String outFolder;
	private WebcamMotionDetector motionDetector;

	public DetectMotionVideoSaver(String outFolder,
			WebcamMotionDetector motionDetector) {
		this.outFolder = outFolder;
		this.webcam = Webcam.getDefault();
		this.motionDetector = motionDetector;
	}

	public DetectMotionVideoSaver(Webcam webcam, String outFolder,
			WebcamMotionDetector motionDetector) {
		this.outFolder = outFolder;
		this.webcam = webcam;
		this.motionDetector = motionDetector;
	}

	@Override
	public void motionDetected(WebcamMotionEvent wme) {
		log.debug("Starting capture! " + wme.getArea());
		File file = new File(outFolder + "output"
				+ new SimpleDateFormat("yyyy-dd-hh-mm-ss").format(new Date())
				+ ".ts");

		IMediaWriter writer = ToolFactory.makeWriter(file.getAbsolutePath());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,
				webcam.getViewSize().width, webcam.getViewSize().height);

		try {
			long start = System.currentTimeMillis();
			int i = 0;
			BufferedImage image;
			IConverter converter;
			IVideoPicture frame;
			while (motionDetector.isMotion()) {
				image = ConverterFactory.convertToType(webcam.getImage(),
						BufferedImage.TYPE_3BYTE_BGR);
				converter = ConverterFactory.createConverter(image,
						IPixelFormat.Type.YUV420P);

				frame = converter.toPicture(image,
						(System.currentTimeMillis() - start) * 1000);
				frame.setKeyFrame(i == 0);
				frame.setQuality(0);
				i++;

				writer.encodeVideo(0, frame);

				// 100 FPS
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} finally {
			writer.close();
			log.debug("Video recorded in file: " + file.getAbsolutePath());
		}
	}

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

	public WebcamMotionDetector getMotionDetector() {
		return motionDetector;
	}

	public void setMotionDetector(WebcamMotionDetector motionDetector) {
		this.motionDetector = motionDetector;
	}

	@Override
	public void onConfigurationChanged() {
		log.debug("************************NEW SETTINGS RECEIVED**************************");

	}
}
