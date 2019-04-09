package com.nedap.university.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nedap.university.communication.PacketReceiver;

class PacketReceiverTest {
	private PacketReceiver testReceiver;
	private DatagramSocket testSocket;
	private DatagramSocket testSendSocket;
	private int testPort = 8080;
	private int testSendPort = 9090;
    public static InetAddress local;
	private DatagramPacket testDatagram;
	private DatagramPacket sentTestDatagram;
	private int packetSize = 15;
	private byte[] testBytePacket;


	@BeforeEach
	void setUp() throws Exception {
		testSocket = new DatagramSocket(testPort);
		testSendSocket = new DatagramSocket(testSendPort);
		testReceiver = new PacketReceiver(testSocket);
		local = InetAddress.getByName("localhost");
		testDatagram = new DatagramPacket(new byte[packetSize], packetSize);
		testBytePacket = "This is a test.".getBytes();
		sentTestDatagram= new DatagramPacket(testBytePacket, testBytePacket.length, local, testPort);


	}

	@AfterEach
	public void closeUp() {
		testSocket.close();
		testSendSocket.close();
	}

	@Test
	void testReceivePacket() throws IOException {
		testSendSocket.send(sentTestDatagram);
		byte[] tempData = testReceiver.receivePacket();
		assertEquals(packetSize, tempData.length);
		assertEquals(testBytePacket[0], tempData[0]);
		assertEquals(testBytePacket[14], tempData[14]);
	}
	
	@Test
	void testGetReceiverPort() throws IOException {
		testSendSocket.send(sentTestDatagram);
		testReceiver.receivePacket();
		assertEquals(testSendPort, testReceiver.getReceiverPort());
	}

	@Test
	void testGetReceiverAddress() throws IOException {
		testSendSocket.send(sentTestDatagram);
		testReceiver.receivePacket();
		assertEquals(local, testReceiver.getReceiverAddress());

	}
}
