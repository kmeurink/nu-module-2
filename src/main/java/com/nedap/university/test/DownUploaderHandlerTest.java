package com.nedap.university.test;

import static org.junit.Assert.*;

import java.io.File;

//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.Before;
import org.junit.Test;

import com.nedap.university.files.DownUploader;
import com.nedap.university.files.DownUploaderHandler;

public class DownUploaderHandlerTest {
	private DownUploaderHandler fileHandlerTest;
	DownUploader downUpload1;
	DownUploader downUpload2;
    private String directory = "testFiles/";
    private File fileDirectory = new File(directory);
    
	@Before
	public void setUp() throws Exception {
		fileHandlerTest = new DownUploaderHandler(fileDirectory);
		downUpload1 = new DownUploader();
		downUpload2 = new DownUploader();
	}

	@Test
	public void testFileHandlerInit() {
		assertTrue(fileHandlerTest.getDownUploads().isEmpty());
	}
/*
	@Test
	public void testAddDownUpload() {
		fileHandlerTest.addDownUpload(downUpload1);
		assertEquals(1, fileHandlerTest.getDownUploads().size());
		fileHandlerTest.addDownUpload(downUpload2);
		assertEquals(2, fileHandlerTest.getDownUploads().size());
	}

	@Test
	public void testRemoveDownUpload() {
		fileHandlerTest.addDownUpload(downUpload1);
		assertEquals(1, fileHandlerTest.getDownUploads().size());
		fileHandlerTest.addDownUpload(downUpload2);
		assertEquals(2, fileHandlerTest.getDownUploads().size());
		fileHandlerTest.removeDownUpload(downUpload1);
		assertEquals(1, fileHandlerTest.getDownUploads().size());
		assertTrue(fileHandlerTest.getDownUploads().contains(downUpload2));
	}*/
}
