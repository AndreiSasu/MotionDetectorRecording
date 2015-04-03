package com.asasu.motiondetect.interfaces;

import java.util.List;

import com.asasu.motiondetect.entity.file.PersistentFileInformation;

public interface IPersistentFileDao {

	public void delete(PersistentFileInformation pf);

	public void update(PersistentFileInformation pf);

	public PersistentFileInformation insert(PersistentFileInformation pf);

	public PersistentFileInformation findByPath(String name);

	public List<PersistentFileInformation> findAll();

	public List<PersistentFileInformation> findByPathAndRemoteId(String path,
			String remoteId);

	public PersistentFileInformation findByPathAndFileSaverId(String filePath,
			Long fileSaverId);

}
