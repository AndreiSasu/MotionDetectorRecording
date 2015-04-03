package com.asasu.motiondetect.entity.settings;

import java.util.Date;

public class Policy {
	private String policyName;
	private int areaThreshold;
	private long inertia;
	private boolean motionDetection;
	private long motionDetectInterval;
	private long motionInertia;
	private String outFolder;
	private String googleRefreshToken;
	private String dropBoxRefreshToken;
	private int streamPort;
	private boolean applied;
	private Date dateSent;
	private Date dateApplied;
	private int id;

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public int getAreaThreshold() {
		return areaThreshold;
	}

	public void setAreaThreshold(int areaThreshold) {
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

	public long getMotionDetectInterval() {
		return motionDetectInterval;
	}

	public void setMotionDetectInterval(long motionDetectInterval) {
		this.motionDetectInterval = motionDetectInterval;
	}

	public long getMotionInertia() {
		return motionInertia;
	}

	public void setMotionInertia(long motionInertia) {
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
