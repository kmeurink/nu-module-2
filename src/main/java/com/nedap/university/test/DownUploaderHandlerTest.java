package com.nedap.university.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nedap.university.files.DownUploader;
import com.nedap.university.files.DownUploaderHandler;

class DownUploaderHandlerTest {
	private DownUploaderHandler fileHandlerTest;
	DownUploader upload1;
	DownUploader upload2;
	DownUploader download1;
	DownUploader download2;
	
	@BeforeEach
	void setUp() throws Exception {
		fileHandlerTest = new DownUploaderHandler();
		upload1 = new DownUploader();
		upload2 = new DownUploader();
		download1 = new DownUploader();
		download2 = new DownUploader();
	}

	@Test
	void testFileHandlerInit() {
		assertTrue(fileHandlerTest.getDownloads().isEmpty());
		assertTrue(fileHandlerTest.getUploads().isEmpty());
	}

	@Test
	void testAddDownload() {
		fileHandlerTest.addDownload(download1);
		assertEquals(1, fileHandlerTest.getDownloads().size());
		fileHandlerTest.addDownload(download2);
		assertEquals(2, fileHandlerTest.getDownloads().size());
	}

	@Test
	void testAddUpload() {
		fileHandlerTest.addUpload(upload1);
		assertEquals(1, fileHandlerTest.getUploads().size());
		fileHandlerTest.addUpload(upload2);
		assertEquals(2, fileHandlerTest.getUploads().size());	
		}

	@Test
	void testRemoveDownload() {
		fileHandlerTest.addDownload(download1);
		assertEquals(1, fileHandlerTest.getDownloads().size());
		fileHandlerTest.addDownload(download2);
		assertEquals(2, fileHandlerTest.getDownloads().size());
		fileHandlerTest.removeDownload(download1);
		assertEquals(1, fileHandlerTest.getDownloads().size());
		assertTrue(fileHandlerTest.getDownloads().contains(download2));
	}

	@Test
	void testRemoveUpload() {
		fileHandlerTest.addUpload(upload1);
		assertEquals(1, fileHandlerTest.getUploads().size());
		fileHandlerTest.addUpload(upload2);
		assertEquals(2, fileHandlerTest.getUploads().size());
		fileHandlerTest.removeUpload(upload2);
		assertFalse(fileHandlerTest.getUploads().contains(upload2));

	}

}
