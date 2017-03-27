package com.asasu.motiondetect.savers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.asasu.motiondetect.GoogleDrive;
import com.asasu.motiondetect.entity.file.FileSaver;
import com.asasu.motiondetect.entity.file.FileSaverDao;
import com.asasu.motiondetect.entity.file.PersistentFileDao;
import com.asasu.motiondetect.entity.file.PersistentFileInformation;
import com.asasu.motiondetect.interfaces.IFileSaver;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import static com.asasu.motiondetect.constants.Constants.outFolder;

import javax.inject.Singleton;

@Singleton
@Component
public class GoogleDriveSaver implements IFileSaver {
    private static final Log log = LogFactory.getLog(GoogleDriveSaver.class);

    private FileSearch fileSearch;
    private FileSaverDao fileSaverDao;
    private PersistentFileDao persistentFileDao;
    private String credentialToken;
    private String fileSaverName = "google";
    private Long id;
    private Drive googleDriveService;
    private boolean authenticated;

    private String remoteFolder;
    private String remoteFolderId;

    public void setRemoteFolder(String remoteFolder) {
        this.remoteFolder = remoteFolder;
    }

    public void setFileSearch(FileSearch fileSearch) {
        this.fileSearch = fileSearch;
    }

    public void setFileSaverDao(FileSaverDao fileSaverDao) {
        this.fileSaverDao = fileSaverDao;
    }

    public void setPersistentFileDao(PersistentFileDao persistentFileDao) {
        this.persistentFileDao = persistentFileDao;
    }

    @Override
    public void run() {
        while (!authenticated) {
            log.info("Not authenticated, sleeping");
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

    @Override
    public void save(File f) {
        log.debug("Saving: " + f.getAbsolutePath());

        try {
            // Insert a file
            com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();

            String fileMimeType = Files.probeContentType(Paths.get(f.getAbsolutePath()));
            body.setName(f.getName());
            body.setDescription(f.getName());
            body.setMimeType(fileMimeType);
            body.setParents(Collections.singletonList(remoteFolderId));

            if (persistentFileDao.findByPathAndFileSaverId(f.getAbsolutePath(),
                    this.id) == null) {
                FileContent mediaContent = new FileContent(fileMimeType, f);
                com.google.api.services.drive.model.File remoteFile = googleDriveService
                        .files().create(body, mediaContent).execute();
                this.insertFile(f, remoteFile.getId());
            }

        } catch (IOException | NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return fileSaverName;
    }

    public void authenticate() throws Exception {
        Credential credential = GoogleDrive.authorize();
        credentialToken = credential.getAccessToken();
        googleDriveService = GoogleDrive.getDriveService(credential);
        this.authenticated = true;
        log.debug("Successfully authenticated ");
        saveToken();
        createRemoteFolderIfItDoesntExist();
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

    private boolean remoteFolderExists() {
        try {
            String pageToken = null;
            do {
                FileList result = googleDriveService.files().list()
                        .setQ("mimeType='application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                for (com.google.api.services.drive.model.File file : result.getFiles()) {
                    if (remoteFolder.equals(file.getName())) {
                        remoteFolderId = file.getId();

                        log.debug(String.format("Found remote folder with name and id: %s (%s)\n", file.getName(), file.getId()));
                        return true;
                    }

                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createRemoteFolderIfItDoesntExist() {
        if (remoteFolderExists()) {
            return;
        }
        try {
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(remoteFolder);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            com.google.api.services.drive.model.File file = googleDriveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + file.getId());
            remoteFolderId = file.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged() {
        log.debug("************************NEW SETTINGS RECEIVED**************************");
        // get new settings from db
        // apply new settings
        // call afterPropertiesSet
    }
}
