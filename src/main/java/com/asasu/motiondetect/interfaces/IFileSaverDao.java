package com.asasu.motiondetect.interfaces;

import java.util.List;

import com.asasu.motiondetect.entity.file.FileSaver;

public interface IFileSaverDao {
	public FileSaver insert(FileSaver fs);

	public void update(FileSaver fs);

	public void delete(FileSaver fs);

	public FileSaver findByName(String fsName);

	public FileSaver findByToken(String token);

	public List<FileSaver> findAll();
}
