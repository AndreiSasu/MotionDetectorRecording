package com.asasu.motiondetect.savers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.asasu.motiondetect.entity.file.FileSaver;
import com.asasu.motiondetect.entity.file.FileSaverDao;
import com.asasu.motiondetect.entity.file.PersistentFileDao;
import com.asasu.motiondetect.entity.file.PersistentFileInformation;
import com.asasu.motiondetect.interfaces.IFileSaver;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;
import static com.asasu.motiondetect.constants.Constants.outFolder;

import javax.inject.Singleton;

@Singleton
@Component
public class DropBoxSaver implements IFileSaver {
	private static final Log log = LogFactory.getLog(DropBoxSaver.class);

	private FileSearch fileSearch;
	private FileSaverDao fileSaverDao;
	private PersistentFileDao persistentFileDao;
	private String credentialToken;
	private String fileSaverName = "dropbox";
	private Long id;
	private DbxClient dropBoxClient;
	private DbxRequestConfig config;
	private boolean authenticated;


    public DropBoxSaver() {
//        if (credentialToken == null
//                && fileSaverDao.findByName(this.fileSaverName) == null) {
//            return;
//        } else if (credentialToken == null) {
//            this.credentialToken = fileSaverDao.findByName(this.fileSaverName)
//                    .getCredentialToken();
//        }
//        log.debug("Authenticating");
//        try {
//            this.authenticate();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        this.saveToken();
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
		if (!authenticated) {
			log.debug("Not authenticated, exiting");
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
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			// insert or update?
			if (persistentFileDao.findByPathAndFileSaverId(f.getAbsolutePath(),
					this.id) == null) {
				DbxEntry.File uploadedFile = dropBoxClient.uploadFile(
						"/" + f.getName(), DbxWriteMode.add(), f.length(),
						inputStream);

				log.debug("Uploaded: " + uploadedFile.toString());
				this.insertFile(f, uploadedFile.rev);
			} else {
				String fileRevision = persistentFileDao
						.findByPathAndFileSaverId(f.getAbsolutePath(), this.id)
						.getRemoteId();
				DbxEntry.File uploadedFile = dropBoxClient.uploadFile(
						"/" + f.getName(), DbxWriteMode.update(fileRevision),
						f.length(), inputStream);
				log.debug("Updated: " + uploadedFile.toString());
				this.updateFile(f, uploadedFile.rev);
			}
		} catch (NoSuchAlgorithmException | DbxException | IOException e) {
			e.printStackTrace();
		}
	}

    @Override
    public String getName() {
        return fileSaverName;
    }

    public void authenticate() throws Exception {
		log.debug(this.credentialToken);
		config = new DbxRequestConfig("MyApp/1.0", Locale.getDefault()
				.toString());
		dropBoxClient = new DbxClient(config, this.credentialToken);
		log.info("Linked account: "
				+ dropBoxClient.getAccountInfo().displayName);
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
	public void onConfigurationChanged() {
		log.debug("************************NEW SETTINGS RECEIVED**************************");
		// get new settings from db
		// apply new settings
		// call authenticate() && saveOrUpdateToken()
	}
}
