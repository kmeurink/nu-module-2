package com.nedap.university.test;

import static org.junit.Assert.*;

//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.Before;
import org.junit.Test;

import com.nedap.university.files.DownUploader;
import com.nedap.university.files.DownUploaderHandler;

public class DownUploaderHandlerTest {
	private DownUploaderHandler fileHandlerTest;
	DownUploader upload1;
	DownUploader upload2;
	DownUploader download1;
	DownUploader download2;
	
	@Before
	public void setUp() throws Exception {
		fileHandlerTest = new DownUploaderHandler();
		upload1 = new DownUploader();
		upload2 = new DownUploader();
		download1 = new DownUploader();
		download2 = new DownUploader();
	}

	@Test
	public void testFileHandlerInit() {
		assertTrue(fileHandlerTest.getDownloads().isEmpty());
		assertTrue(fileHandlerTest.getUploads().isEmpty());
	}

	@Test
	public void testAddDownload() {
		fileHandlerTest.addDownload(download1);
		assertEquals(1, fileHandlerTest.getDownloads().size());
		fileHandlerTest.addDownload(download2);
		assertEquals(2, fileHandlerTest.getDownloads().size());
	}

	@Test
	public void testAddUpload() {
		fileHandlerTest.addUpload(upload1);
		assertEquals(1, fileHandlerTest.getUploads().size());
		fileHandlerTest.addUpload(upload2);
		assertEquals(2, fileHandlerTest.getUploads().size());	
		}

	@Test
	public void testRemoveDownload() {
		fileHandlerTest.addDownload(download1);
		assertEquals(1, fileHandlerTest.getDownloads().size());
		fileHandlerTest.addDownload(download2);
		assertEquals(2, fileHandlerTest.getDownloads().size());
		fileHandlerTest.removeDownload(download1);
		assertEquals(1, fileHandlerTest.getDownloads().size());
		assertTrue(fileHandlerTest.getDownloads().contains(download2));
	}

	@Test
	public void testRemoveUpload() {
		fileHandlerTest.addUpload(upload1);
		assertEquals(1, fileHandlerTest.getUploads().size());
		fileHandlerTest.addUpload(upload2);
		assertEquals(2, fileHandlerTest.getUploads().size());
		fileHandlerTest.removeUpload(upload2);
		assertFalse(fileHandlerTest.getUploads().contains(upload2));

	}

}
