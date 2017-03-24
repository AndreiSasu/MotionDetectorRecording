package com.asasu.motiondetect.savers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asasu.motiondetect.entity.file.PersistentFileDao;

public class FileSearch {
	private static final Log log = LogFactory.getLog(FileSearch.class);

	private String fileNameToSearch;
	private ArrayList<String> modifiedFileNames = new ArrayList<String>();
	private Long fileSaverId;
	private PersistentFileDao persistentFileDao;

	public void setPersistentFileDao(PersistentFileDao persistentFileDao) {
		this.persistentFileDao = persistentFileDao;
	}

	public boolean hasNewFiles() {
		return modifiedFileNames.size() > 0;
	}

	public String getFileNameToSearch() {
		return fileNameToSearch;
	}

	public ArrayList<String> getModifiedFilenames() {
		return this.modifiedFileNames;
	}

	public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}

	public void setFileSaverId(Long id) {
		this.fileSaverId = id;
	}

	public Long getFileSaverId() {
		return this.fileSaverId;
	}

	public boolean isFileLocked(File f) {
		FileChannel channel;
		FileLock lock;
		try {
			channel = new RandomAccessFile(f, "rw").getChannel();
			lock = channel.lock();
		} catch (IOException | OverlappingFileLockException e) {
			// e.printStackTrace();
			return true;
		}
		try {
			lock.release();
			channel.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return false;
	}

	public String fileToMd5(File file) throws NoSuchAlgorithmException,
			IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(file);
		DigestInputStream dis = new DigestInputStream(is, md);
		byte[] data = new byte[64000];
		while ((dis.read(data, 0, data.length)) != -1) {

		}
		byte[] digest = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		log.debug("Digest(in hex format):: " + file.getAbsolutePath() + " "
				+ sb.toString());
		dis.close();
		return sb.toString();
	}

	public void searchDirectory(File directory)
			throws NoSuchAlgorithmException, IOException {
		if (directory.isDirectory()) {
			search(directory);
		} else {
			log.error(directory.getAbsoluteFile() + " is not a directory!");
		}

	}

	private void search(File file) throws NoSuchAlgorithmException, IOException {
		if (file.isDirectory()) {
			log.debug("Searching directory ... " + file.getAbsoluteFile());
			// do you have permission to read this directory?
			if (file.canRead() && file.listFiles() != null) {
				for (File temp : file.listFiles()) {
					String tempPath = temp.getAbsolutePath();
					if (temp.isDirectory()) {
						search(temp);
					}
					if (isFileLocked(temp)) {
						return;
					}
					if (!modifiedFileNames.contains(tempPath)
							&& persistentFileDao.findByPathAndFileSaverId(
									tempPath, this.getFileSaverId()) == null) {
						modifiedFileNames.add(tempPath);
					}
					if (persistentFileDao.findByPathAndFileSaverId(tempPath,
							this.getFileSaverId()) == null) {
						modifiedFileNames.add(tempPath);
					} else if (temp.lastModified() != persistentFileDao
							.findByPathAndFileSaverId(tempPath,
									this.getFileSaverId()).getLastModified()) {
						modifiedFileNames.add(tempPath);
					} else {
						log.debug("Pruning file: " + tempPath);
						temp.delete();
                        assert !temp.exists();
					}
				}
			}

		} else {
			log.info(file.getAbsoluteFile() + " Permission Denied");
		}
	}
}