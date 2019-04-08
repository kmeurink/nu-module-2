package com.nedap.university.test;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nedap.university.files.DownUploader;

class DownUploaderTest {
	private DownUploader downUpTest;
	private String testName = "testFile.txt";
	private byte[] dataPart1;
	private byte[] dataPart2;

	@BeforeEach
	void setUp() throws Exception {
		downUpTest = new DownUploader();
		dataPart1 = new byte[499];
		dataPart2 = new byte[499];
	}

	@Test
	void testDownUploaderInit() {
		assertTrue(downUpTest.sendData().isEmpty());
	}

	@Test
	void testSetGetName() {
		downUpTest.setName("test.txt");
		assertEquals("test.txt", downUpTest.getName());
		
	}

	@Test
	void testSetGetSize() {
		assertEquals(0, downUpTest.getSize());
		downUpTest.setSize(10);
		assertEquals(10, downUpTest.getSize());
	}

	@Test
	void testAddData() {
		assertEquals(0,downUpTest.determineSize());
		downUpTest.addData(dataPart1);
		assertEquals(499,downUpTest.determineSize());
		downUpTest.addData(dataPart2);
		assertEquals(998,downUpTest.determineSize());
	}
	
	@Test
	void testReadOutFile() {
		downUpTest.readOutFile("src/main/java/com/nedap/university/test/testFile.txt");
		assertEquals(3, downUpTest.sendData().size());
	}

	@Test
	void testFinishFiletxt() {
		downUpTest.readOutFile("src/main/java/com/nedap/university/test/testFile.txt");
		List<byte[]> tempFilepre = downUpTest.sendData();
		downUpTest.setName("src/main/java/com/nedap/university/test/testFile2.txt");
		downUpTest.finishFile();
		downUpTest.readOutFile("src/main/java/com/nedap/university/test/testFile2.txt");
		List<byte[]> tempFilepost = downUpTest.sendData();
		assertArrayEquals(tempFilepre.get(0), tempFilepost.get(0));
		assertArrayEquals(tempFilepre.get(1), tempFilepost.get(1));
		assertArrayEquals(tempFilepre.get(2), tempFilepost.get(2));
	}
	
	@Test
	void testFinishFilepdf() {
		downUpTest.readOutFile("src/main/java/com/nedap/university/test/ns-2019-col4.pdf");
		List<byte[]> tempFilepre = downUpTest.sendData();
		downUpTest.setName("src/main/java/com/nedap/university/test/ns-2019-col4copy.pdf");
		downUpTest.finishFile();
		downUpTest.readOutFile("src/main/java/com/nedap/university/test/ns-2019-col4copy.pdf");
		List<byte[]> tempFilepost = downUpTest.sendData();
		assertArrayEquals(tempFilepre.get(0), tempFilepost.get(0));
		assertArrayEquals(tempFilepre.get(1), tempFilepost.get(1));
		assertArrayEquals(tempFilepre.get(2), tempFilepost.get(2));
	}

	@Test
	void testSendDataSingle() {
		downUpTest.readOutFile("src/main/java/com/nedap/university/test/testFile.txt");
		byte[] dateBlock= {115, 46};
		assertEquals(dateBlock[0],downUpTest.sendDataSingle(2)[0]);
		assertEquals(dateBlock[1],downUpTest.sendDataSingle(2)[1]);
	}

	@Test
	void testSetSeqNumber() {
		fail("Not yet implemented");
	}

	@Test
	void testSetAckNumber() {
		fail("Not yet implemented");
	}

}
