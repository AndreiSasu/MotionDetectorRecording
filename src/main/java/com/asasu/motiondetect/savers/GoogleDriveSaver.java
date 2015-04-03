package com.asasu.motiondetect.savers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asasu.motiondetect.entity.file.FileSaver;
import com.asasu.motiondetect.entity.file.FileSaverDao;
import com.asasu.motiondetect.entity.file.PersistentFileDao;
import com.asasu.motiondetect.entity.file.PersistentFileInformation;
import com.asasu.motiondetect.interfaces.IFileSaver;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

public class GoogleDriveSaver implements IFileSaver {
	private static final Log log = LogFactory.getLog(GoogleDriveSaver.class);

	private FileSearch fileSearch;
	private FileSaverDao fileSaverDao;
	private PersistentFileDao persistentFileDao;
	private String outFolder;
	private String credentialToken;
	private String fileSaverName = "google";
	private Long id;
	private Drive googleDriveService;
	private boolean authenticated;

	public void setOutFolder(String outFolder) {
		this.outFolder = outFolder;
	}

	public void setFileSearch(FileSearch fileSearch) {
		this.fileSearch = fileSearch;
	}

	public void setFileSaverDao(FileSaverDao fileSaverDao) {
		this.fileSaverDao = fileSaverDao;
	}

	public void setCredentialToken(String credentialToken) {
		this.credentialToken = credentialToken;
	}

	public void setPersistentFileDao(PersistentFileDao persistentFileDao) {
		this.persistentFileDao = persistentFileDao;
	}

	@Override
	public void run() {
		while (!authenticated) {
			log.debug("Not authenticated, exiting");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (true) {
			try {
				fileSearch.searchDirectory(new File(outFolder));
			} catch (NoSuchAlgorithmException | IOException e1) {
				e1.printStackTrace();
			}
			if (fileSearch.hasNewFiles()) {
				ArrayList<String> modifiedFilenames = fileSearch
						.getModifiedFilenames();
				for (int i = 0; i < modifiedFilenames.size(); i++) {
					this.save(new File(modifiedFilenames.remove(i)));
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void insertFile(File f, String remoteId)
			throws NoSuchAlgorithmException, IOException {
		PersistentFileInformation pfi = new PersistentFileInformation();
		pfi.setFilePath(f.getAbsolutePath());
		pfi.setMd5(this.fileSearch.fileToMd5(f));
		pfi.setLastModified(f.lastModified());
		pfi.setFileSaverId(this.id);
		pfi.setRemoteId(remoteId);
		persistentFileDao.insert(pfi);
	}

	private void updateFile(File f, String remoteId)
			throws NoSuchAlgorithmException, IOException {
		PersistentFileInformation pfi = persistentFileDao.findByPath(f
				.getAbsolutePath());
		pfi.setMd5(this.fileSearch.fileToMd5(f));
		pfi.setLastModified(f.lastModified());
		pfi.setFileSaverId(this.id);
		pfi.setRemoteId(remoteId);
		persistentFileDao.update(pfi);
	}

	@Override
	public void save(File f) {
		log.debug("Saving: " + f.getAbsolutePath());
		// Insert a file
		com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
		try {
			if (persistentFileDao.findByPathAndFileSaverId(f.getAbsolutePath(),
					this.id) == null) {
				String fileMimeType = Files.probeContentType(Paths.get(f
						.getAbsolutePath()));
				body.setTitle(f.getName());
				body.setDescription(f.getName());
				body.setMimeType(fileMimeType);

				FileContent mediaContent = null;
				mediaContent = new FileContent(fileMimeType, f);
				com.google.api.services.drive.model.File remoteFile = googleDriveService
						.files().insert(body, mediaContent).execute();
				this.insertFile(f, remoteFile.getId());
			} else {
				String fileMimeType = Files.probeContentType(Paths.get(f
						.getAbsolutePath()));
				String fileId = persistentFileDao.findByPathAndFileSaverId(
						f.getAbsolutePath(), this.id).getRemoteId();
				com.google.api.services.drive.model.File remoteFile = googleDriveService
						.files().get(fileId).execute();
				FileContent mediaContent = new FileContent(fileMimeType, f);
				com.google.api.services.drive.model.File updatedFile = googleDriveService
						.files().update(fileId, remoteFile, mediaContent)
						.execute();
				this.updateFile(f, updatedFile.getId());
			}
		} catch (IOException | NoSuchAlgorithmException e2) {
			e2.printStackTrace();
		}
	}

	public void authenticate() throws Exception {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		log.debug(this.credentialToken);

		GoogleCredential credential = new GoogleCredential()
				.setAccessToken(this.credentialToken);
		googleDriveService = new Drive.Builder(httpTransport, jsonFactory,
				credential).setApplicationName("Test App").build();
		this.authenticated = true;
		log.debug("Successfully authenticated ");
	}

	private void saveToken() {
		log.debug("Loading persistence data and injecting into "
				+ fileSearch.getClass());
		FileSaver fs = new FileSaver();
		if (fileSaverDao.findByToken(credentialToken) == null) {
			fs.setCredentialToken(credentialToken);
			fs.setFileSaverName(fileSaverName);
			fs = fileSaverDao.insert(fs);
			this.id = fs.getId();
			fileSearch.setFileSaverId(this.id);
		} else {
			fs = fileSaverDao.findByToken(credentialToken);
			this.id = fs.getId();
			fileSearch.setFileSaverId(this.id);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (credentialToken == null
				&& fileSaverDao.findByName(this.fileSaverName) == null) {
			return;
		} else if (credentialToken == null) {
			this.credentialToken = fileSaverDao.findByName(this.fileSaverName)
					.getCredentialToken();
		}
		log.debug("Authenticating ");
		this.authenticate();
		this.saveToken();
	}

	@Override
	public void destroy() throws Exception {
		log.debug("Cleaning up after myself");
	}

	@Override
	public void onConfigurationChanged() {
		log.debug("************************NEW SETTINGS RECEIVED**************************");
		// get new settings from db
		// apply new settings
		// call afterPropertiesSet
	}
}
