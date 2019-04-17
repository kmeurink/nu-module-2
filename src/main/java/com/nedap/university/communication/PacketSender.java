package com.nedap.university.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
/**@deprecated
 * Sends the packets to the server or client.
 * @author kester.meurink
 *
 */
public class PacketSender extends Thread{ //TODO perhaps better as a method, but is used by both server and client. Also has to be given the socket to work.
	//Named Constants:
    private DatagramSocket socket;
    public static InetAddress BROADCAST;
    private InetAddress serverAddress;
    private DatagramPacket packetToSend;
    private BlockingQueue sendingQueue;
    private int queueLength = 1000;
    private boolean running = true;
    private InetAddress sendingAddress;
    private int sendingPort;
	
	//Constructors:
	public PacketSender(DatagramSocket socket, InetAddress address, int port) throws UnknownHostException {
		this.sendingQueue = new ArrayBlockingQueue(queueLength);
		this.socket = socket;
		this.BROADCAST = InetAddress.getByName("255.255.255.255"); //TODO not neccessary here.
		this.sendingAddress = address;
		this.sendingPort = port;
	}
	
	public void run() {
		while(running) {
			if (!sendingQueue.isEmpty()) {
				try {
					sendPacket((DatagramPacket) sendingQueue.take());
					System.out.println("Sending packet");
				} catch (InterruptedException e) {
					// TODO handle error
					e.printStackTrace();
				}
			}
		}
	}
	
	//Queries:
	
	/**
	 * Returns the last created datagram packet.
	 * @return
	 */
	public DatagramPacket getDatagram() {
		return this.packetToSend;
	}
	
	
	//Commands:
	/**
	 * Used to set client destination address to the actual server ip.
	 * @param server
	 */
	public void setAddress(InetAddress server) {
		this.sendingAddress = server;
	}
	
	/**
	 * Builds the datagram packet that is to be sent to the receiver.
	 * @param receiver
	 * @param receiverPort
	 */
	public DatagramPacket buildDatagram(InetAddress receiver, int receiverPort, byte[] packet) {
		packetToSend = new DatagramPacket(packet, packet.length, receiver, receiverPort);
		return packetToSend;
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
	
	/**
	 * Adds a list of packets to the queue or a single one depending on the argument given.
	 * @param list
	 */
	public void addToQueue(List<byte[]> list) {//TODO check for errors
		for(byte[] i : list) {
			this.sendingQueue.add(buildDatagram(this.sendingAddress, this.sendingPort, i));
			//System.out.println("Item from Packetlist added to queue.");
		}
		//System.out.println("Packetlist added to queue.");
	}	

	
	public void addToQueue(byte[] array) {
		this.sendingQueue.add(buildDatagram(this.sendingAddress, this.sendingPort, array));
		//System.out.println("Packet added to queue.");
	}
	
}
