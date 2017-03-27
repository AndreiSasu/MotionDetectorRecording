package com.asasu.motiondetect.entity.settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import com.asasu.motiondetect.interfaces.IFileSaver;

@Named("settingsPolicy")
public class SettingsPolicy {
	private String policyName = "DEFAULT";
    private int pixelThreshold = 25;
    private double areaThreshold = 0.2;
    private long inertia = 500;
    private boolean motionDetection = true;
    private int motionDetectInterval = 500;
    private int motionInertia = 10000;
    private String outFolder;
    private String googleRefreshToken;
    private String dropBoxRefreshToken;
    private int streamPort;
    private boolean applied = true;
    private Date dateSent;
    private Date dateApplied;
    private int id;
    private String remoteFolder = "MotionDetectorRecording";

    public String getRemoteFolder() {
        return remoteFolder;
    }

    public void setRemoteFolder(String remoteFolder) {
        this.remoteFolder = remoteFolder;
    }

    public List<IFileSaver> getFileSavers() {
        return fileSavers;
    }

    public void setFileSavers(List<IFileSaver> fileSavers) {
        this.fileSavers = fileSavers;
    }

    private List<IFileSaver> fileSavers = new ArrayList<>();

    public int getPixelThreshold() {
        return pixelThreshold;
    }

    public void setPixelThreshold(int pixelThreshold) {
        this.pixelThreshold = pixelThreshold;
    }

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public double getAreaThreshold() {
		return areaThreshold;
	}

	public void setAreaThreshold(double areaThreshold) {
		this.areaThreshold = areaThreshold;
	}

	public long getInertia() {
		return inertia;
	}

	public void setInertia(long inertia) {
		this.inertia = inertia;
	}

	public boolean isMotionDetection() {
		return motionDetection;
	}

	public void setMotionDetection(boolean motionDetection) {
		this.motionDetection = motionDetection;
	}

	public int getMotionDetectInterval() {
		return motionDetectInterval;
	}

	public void setMotionDetectInterval(int motionDetectInterval) {
		this.motionDetectInterval = motionDetectInterval;
	}

	public int getMotionInertia() {
		return motionInertia;
	}

	public void setMotionInertia(int motionInertia) {
		this.motionInertia = motionInertia;
	}

	public String getOutFolder() {
		return outFolder;
	}

	public void setOutFolder(String outFolder) {
		this.outFolder = outFolder;
	}

	public String getGoogleRefreshToken() {
		return googleRefreshToken;
	}

	public void setGoogleRefreshToken(String googleRefreshToken) {
		this.googleRefreshToken = googleRefreshToken;
	}

	public String getDropBoxRefreshToken() {
		return dropBoxRefreshToken;
	}

	public void setDropBoxRefreshToken(String dropBoxRefreshToken) {
		this.dropBoxRefreshToken = dropBoxRefreshToken;
	}

	public int getStreamPort() {
		return streamPort;
	}

	public void setStreamPort(int streamPort) {
		this.streamPort = streamPort;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	public Date getDateApplied() {
		return dateApplied;
	}

	public void setDateApplied(Date dateApplied) {
		this.dateApplied = dateApplied;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
