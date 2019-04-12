package com.nedap.university.test;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.zip.CRC32;

import org.junit.Before;
import org.junit.Test;

import com.nedap.university.communication.PacketBuilder;

public class PacketBuilderTest {
	private PacketBuilder testBuilder;
	private int packetLength = 1024;
	private int headerSize = 22;
	private int dataLength = packetLength - headerSize;
	private byte testEmptyFlags = 0;
	private byte testEmptyCommands = 0;
	//Test variables:
	private short testFileNumber1 = 126;
	private byte[] testFileNumber1Bytes = ByteBuffer.allocate(2).putShort(testFileNumber1).array();
	private short testFileNumber2 = 31054;
	private byte[] testFileNumber2Bytes = ByteBuffer.allocate(2).putShort(testFileNumber2).array();
	
	int seqAckTest1 = 126;
	byte[] seqAckTest1Bytes = ByteBuffer.allocate(4).putInt(seqAckTest1).array();
	int seqAckTest2 = 31458;
	byte[] seqAckTest2Bytes = ByteBuffer.allocate(4).putInt(seqAckTest2).array();
	int seqAckTest3 = 8200000;
	byte[] seqAckTest3Bytes = ByteBuffer.allocate(4).putInt(seqAckTest3).array();
	int seqAckTest4 = 2100321543;
	byte[] seqAckTest4Bytes = ByteBuffer.allocate(4).putInt(seqAckTest4).array();
	
	private byte testFlagAck = (byte) 2;
	private byte testFlagAckDown = (byte) 16;
	private byte testCommand1 = (byte) 41;
	private byte testCommand2 = (byte) 4;
	
	private short window1 = 150;
	private byte[] window1Bytes = ByteBuffer.allocate(2).putShort(window1).array();
	private short window2 = 31567;
	private byte[] window2Bytes = ByteBuffer.allocate(2).putShort(window2).array();
	
	private long checkSum1 = 210;
	private byte[] checkSum1Bytes = ByteBuffer.allocate(8).putLong(checkSum1).array();
	private long checkSum2 = 24742;
	private byte[] checkSum2Bytes = ByteBuffer.allocate(8).putLong(checkSum2).array();
	
	private Random random = new Random();
	private long fileValue = 2299019777671211267L;
	
	private byte[] testFile = ByteBuffer.allocate(8).putLong(fileValue).array();
	private CRC32 checksumTester = new CRC32();

	
	@Before
	public void setUp() throws Exception {
		testBuilder = new PacketBuilder(dataLength, packetLength);
	}
	
	@Test
	public void testPacketBuilderInit() {
		assertEquals(headerSize, testBuilder.getHeader().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getHeader()));
		assertEquals(dataLength, testBuilder.getData().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getData()));
		assertEquals(packetLength, testBuilder.getPacket().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getPacket()));

	}
	
	private boolean checkContents(byte expected, byte[] array) {
		int byteCount = 0;
		boolean expectedContents = true;
		for (byte i : array) {
			if (i != expected) {
				byteCount++;
			}
		}
		if (byteCount != 0) {
			expectedContents = false;
		}
		return expectedContents;
	}
	
	//First testing the queries:
	@Test
	public void testGetFileNumber() {
		assertEquals(2, testBuilder.getFileNumber().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getFileNumber()));
	}
	
	@Test
	public void testGetSeqNumber() {
		assertEquals(4, testBuilder.getSeqNumber().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getSeqNumber()));
	}
	
	@Test
	public void testGetAckNumber() {
		assertEquals(4, testBuilder.getAckNumber().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getAckNumber()));
	}

	@Test
	public void testGetFlags() {
		assertEquals(testEmptyFlags, testBuilder.getFlags());
	}
	
	@Test
	public void testGetCommands() {
		assertEquals(testEmptyCommands, testBuilder.getCommands());
	}

	@Test
	public void testGetWindowSize() {
		assertEquals(2, testBuilder.getWindowSize().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getWindowSize()));
	}

	@Test
	public void testGetCheckSum() {
		assertEquals(8, testBuilder.getCheckSum().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getCheckSum()));
	}

	
	
	//Now testing the commands:
	
	@Test
	public void testSetFileNumber() {
		testBuilder.setFileNumber(testFileNumber1);
		assertArrayEquals(this.testFileNumber1Bytes, testBuilder.getFileNumber());
		testBuilder.setFileNumber(testFileNumber2);
		assertArrayEquals(this.testFileNumber2Bytes, testBuilder.getFileNumber());
	}
	
	@Test
	public void testSetSeqNumber() {
		testBuilder.setSeqNumber(seqAckTest1);
		assertArrayEquals(seqAckTest1Bytes, testBuilder.getSeqNumber());
		testBuilder.setSeqNumber(seqAckTest2);
		assertArrayEquals(seqAckTest2Bytes, testBuilder.getSeqNumber());
		testBuilder.setSeqNumber(seqAckTest3);
		assertArrayEquals(seqAckTest3Bytes, testBuilder.getSeqNumber());
		testBuilder.setSeqNumber(seqAckTest4);
		assertArrayEquals(seqAckTest4Bytes, testBuilder.getSeqNumber());
		
	}

	@Test
	public void testSetAckNumber() {
		testBuilder.setAckNumber(seqAckTest1);
		assertArrayEquals(seqAckTest1Bytes, testBuilder.getAckNumber());
		testBuilder.setAckNumber(seqAckTest2);
		assertArrayEquals(seqAckTest2Bytes, testBuilder.getAckNumber());
		testBuilder.setAckNumber(seqAckTest3);
		assertArrayEquals(seqAckTest3Bytes, testBuilder.getAckNumber());
		testBuilder.setAckNumber(seqAckTest4);
		assertArrayEquals(seqAckTest4Bytes, testBuilder.getAckNumber());
	}

	@Test
	public void testSetFlags() {
		testBuilder.setFlags(testFlagAck);
		assertEquals(this.testFlagAck, testBuilder.getFlags());
		testBuilder.setFlags(testFlagAckDown);
		assertEquals(this.testFlagAckDown, testBuilder.getFlags());
	}

	@Test
	public void testSetCommands() {
		testBuilder.setCommands(testCommand1);
		assertEquals(this.testCommand1, testBuilder.getCommands());
		testBuilder.setCommands(testCommand2);
		assertEquals(this.testCommand2, testBuilder.getCommands());
	}
	
	@Test
	public void testSetWindowSize() {
		testBuilder.setWindowSize(window1);
		assertArrayEquals(window1Bytes, testBuilder.getWindowSize());
		testBuilder.setWindowSize(window2);
		assertArrayEquals(window2Bytes, testBuilder.getWindowSize());
	}

	@Test
	public void testSetCheckSum() {
		testBuilder.setCheckSum(checkSum1Bytes);
		assertArrayEquals(checkSum1Bytes, testBuilder.getCheckSum());
		testBuilder.setCheckSum(checkSum2Bytes);
		assertArrayEquals(checkSum2Bytes, testBuilder.getCheckSum());
	}
	
	@Test
	public void testCalculateChecksum() {
		checksumTester.update(testFile);
		long testFileCRC = checksumTester.getValue();
		
		byte[] testCRC = testBuilder.calculateCheckSum(testFile);
		assertEquals(8, testCRC.length);
	    ByteBuffer buffer = ByteBuffer.wrap(testCRC);
	    assertEquals(testFileCRC, buffer.getLong());
	}
/*
	@Test
	public void testSetHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetData() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPacket() {
		fail("Not yet implemented");
	}
	*/
}
