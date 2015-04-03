package com.asasu.motiondetect.entity.file;

import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

public class PersistentFileTest {
	private org.apache.commons.dbcp.BasicDataSource bds;
	private PersistentFileDao pf;

	public PersistentFileTest() {
		System.out.println("inside constructor");
		bds = new BasicDataSource();
		bds.setDriverClassName("org.h2.Driver");
		bds.setUrl("jdbc:h2:file:~/file_db");
		pf = new PersistentFileDao();
		pf.setDataSource(bds);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdate() {
		PersistentFileInformation pfi = pf.findByPath("C:\\Users");
		pfi.setMd5("This is new md5");
		pf.update(pfi);
		this.testFindAll();
	}

	@Test
	public void testFindByPath() {
		pf.findByPath("C:\\Users");
	}

	@Test
	public void testFindAll() {
		for (PersistentFileInformation pfi : pf.findAll()) {
			System.out.println(" " + pfi.getFilePath() + " " + pfi.getMd5()
					+ " " + pfi.getLastModified() + " " + pfi.getFileSaverId()
					+ " " + pfi.getRemoteId());
		}
	}

	@Test
	public void testSetDataSource() {

	}

	@Test
	public void testInsert() {
		PersistentFileInformation pfi = new PersistentFileInformation();
		pfi.setFilePath("D:\\tmp");
		pfi.setLastModified(new File("D:\\tmp").lastModified());
		System.out.println(pfi.getLastModified());
		pfi.setMd5("MD5 PLACEHOLDER");
		System.out.println("inserting: " + pfi.getFilePath() + " "
				+ pfi.getMd5() + " " + pfi.getLastModified());
		pf.insert(pfi);
	}
}
