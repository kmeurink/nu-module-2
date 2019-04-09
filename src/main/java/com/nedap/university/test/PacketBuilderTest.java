package com.nedap.university.test;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import com.nedap.university.communication.PacketBuilder;

public class PacketBuilderTest {
	private PacketBuilder testBuilder;
	private int dataLength = 499;
	private int packetLength = 512;
	private byte testEmptyFlags = 0;
	//Test variables:
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
	
	private short window1 = 150;
	private byte[] window1Bytes = ByteBuffer.allocate(2).putShort(window1).array();;
	private short window2 = 31567;
	private byte[] window2Bytes = ByteBuffer.allocate(2).putShort(window2).array();;
	
	private short checkSum1 = 210;
	private byte[] checkSum1Bytes = ByteBuffer.allocate(2).putShort(checkSum1).array();;
	private short checkSum2 = 24742;
	private byte[] checkSum2Bytes = ByteBuffer.allocate(2).putShort(checkSum2).array();;
	
	
	@Before
	public void setUp() throws Exception {
		testBuilder = new PacketBuilder(dataLength, packetLength);
	}

	@Test
	public void testPacketBuilderInit() {
		assertEquals(13, testBuilder.getHeader().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getHeader()));
		assertEquals(499, testBuilder.getData().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getData()));
		assertEquals(512, testBuilder.getPacket().length);
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
	public void testGetWindowSize() {
		assertEquals(2, testBuilder.getWindowSize().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getWindowSize()));
	}

	@Test
	public void testGetCheckSum() {
		assertEquals(2, testBuilder.getCheckSum().length);
		assertTrue(this.checkContents((byte) 0, testBuilder.getCheckSum()));
	}

	//Now testing the commands:
	
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
	public void testSetWindowSize() {
		testBuilder.setWindowSize(window1);
		assertArrayEquals(window1Bytes, testBuilder.getWindowSize());
		testBuilder.setWindowSize(window2);
		assertArrayEquals(window2Bytes, testBuilder.getWindowSize());
	}

	@Test
	public void testSetCheckSum() {
		testBuilder.setCheckSum(checkSum1);
		assertArrayEquals(checkSum1Bytes, testBuilder.getCheckSum());
		testBuilder.setCheckSum(checkSum2);
		assertArrayEquals(checkSum2Bytes, testBuilder.getCheckSum());
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
