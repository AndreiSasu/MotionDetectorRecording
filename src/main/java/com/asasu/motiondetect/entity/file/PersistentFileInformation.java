package com.asasu.motiondetect.entity.file;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "file_information")
public class PersistentFileInformation {

	private String filePath;
	private String md5;
	private String remoteId;
	private Long lastModified;
	private Long id;
	private Long fileSaverId;

	public String getRemoteId() {
		return this.remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFileSaverId(Long id) {
		this.fileSaverId = id;
	}

	public Long getFileSaverId() {
		return this.fileSaverId;
	}

}
