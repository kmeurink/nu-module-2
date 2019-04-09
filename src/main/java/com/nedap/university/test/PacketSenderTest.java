package com.nedap.university.test;

//import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;

import com.nedap.university.communication.PacketSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class PacketSenderTest {
	private PacketSender testSender;
	private DatagramSocket testSocket;
	private DatagramSocket testReceiverSocket;
	private DatagramPacket receivedPacket;
	private int packetSize = 15;
	private int testPort = 8080;
	private byte[] testBytePacket;
    public static InetAddress BROADCAST;
    public int receiverTestPort = 9090;
    public static InetAddress local;

	@Before
	public void setUp() throws Exception {
		testSocket = new DatagramSocket(testPort);
		testReceiverSocket = new DatagramSocket(receiverTestPort);
		testSender = new PacketSender(testSocket);
		testBytePacket = "This is a test.".getBytes();
		this.BROADCAST = InetAddress.getByName("255.255.255.255");
		this.local = InetAddress.getByName("localhost");
		receivedPacket = new DatagramPacket(new byte[packetSize], packetSize);
	}

	@After
	public void closeUp() {
		testReceiverSocket.close();
		testSocket.close();
	}

	@Test
	public void testBuildDatagram() {
		testSender.buildDatagram(BROADCAST, receiverTestPort, testBytePacket);
		assertEquals(BROADCAST, testSender.getDatagram().getAddress());
		assertEquals(receiverTestPort, testSender.getDatagram().getPort());
		assertArrayEquals(testBytePacket, testSender.getDatagram().getData());
	}

	@Test
	public void testSendPacket() throws IOException {
		DatagramPacket testDatagram= new DatagramPacket(testBytePacket, testBytePacket.length, local, receiverTestPort);
		testSender.sendPacket(testDatagram);
		this.testReceiverSocket.receive(receivedPacket);
		assertArrayEquals(testBytePacket, this.receivedPacket.getData());
		assertEquals(testBytePacket[0], this.receivedPacket.getData()[0]);
		assertEquals(testBytePacket[14], this.receivedPacket.getData()[14]);
	}

}
