package com.nedap.university.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * Sends the packets to the server or client.
 * @author kester.meurink
 *
 */
public class PacketSender { //TODO perhaps better as a method, but is used by both server and client. Also has to be given the socket to work.
	//Named Constants:
    private DatagramSocket socket;
    public static InetAddress BROADCAST;
    private InetAddress serverAddress;
    private DatagramPacket packetToSend;
	
	//Constructors:
	public PacketSender(DatagramSocket socket) throws UnknownHostException {
		this.socket = socket;
		this.BROADCAST = InetAddress.getByName("255.255.255.255");
	}
	
	//Queries:
	
	
	
	//Commands:
	
	/**
	 * Builds the datagram packet that is to be sent to the receiver.
	 * @param receiver
	 * @param receiverPort
	 */
	public void buildDatagram(InetAddress receiver, int receiverPort, byte[] packet) {
		packetToSend = new DatagramPacket(packet, packet.length, receiver, receiverPort);
		
	}
	
	/**
	 * Sends a datagram packet to a receiver. 
	 * @param packet
	 */
	public void sendPacket(DatagramPacket packet) { //TODO perhaps remove the argument, as it will always be the packetToSend
		try {
			socket.send(packet);
		} catch (IOException e) { //TODO add handling of error
			e.printStackTrace();
		}
	}
}
