package com.nedap.university.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Class handles reliable transfer of packets between client and server.
 * @author kester.meurink
 *
 */
public class TransferProtocol {

	//Named constants:
    private BlockingQueue sendingQueue;
    private BlockingQueue receivingQueue;
    private int queueLength = 1000;
    private boolean sending = true;
    private DatagramSocket socket;
	private int packetSize = 1024;

	//Constructors:
	public TransferProtocol(DatagramSocket socket) {
		this.sendingQueue = new ArrayBlockingQueue(queueLength);
		this.receivingQueue = new ArrayBlockingQueue(queueLength);
		this.socket = socket;
		
	}
	
	//Queries:
	
	
	//Commands:
	public void queueSender() {
		Thread senderThread = new Thread() {
			public void run() {
				while(true) {
					while(sending) {
						try {
							socket.send((DatagramPacket) sendingQueue.take());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
		};
		senderThread.start();
	}
	
	
	public void queueReceiver() {
		Thread receiverThread = new Thread() {
			public void run() {
				while(true) {
					//while(sending) {
						try {
				            DatagramPacket request = new DatagramPacket(new byte[packetSize], packetSize); //TODO the large packetsize might cause problems for smaller packets when compiling the data together. Need to distinguish useful data.
				            socket.receive(request);
				        	byte[] packetData;
				            int dataLength = request.getLength(); //Determine the actual amount of data
				            packetData = new byte[dataLength];
				            byte[] totalPacket = new byte[packetSize];
				            totalPacket = request.getData();
				            for(int i = 0; i < dataLength; i++) { //Loop through the packet and only assign the bytes containing data.
				            	packetData[i] = totalPacket[i];
				            }
				            receivingQueue.add(packetData);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					//}
				}
				
			}
		};
		receiverThread.start();
	}

	/**
	 * Determines if the protocol is allowed to send packets in the queue.
	 * @param status
	 */
	public void setSending(boolean status) {
		this.sending = status;
	}
	
	/**
	 * Returns a dataPacket from the receivingqueue.
	 * @return
	 * @throws InterruptedException
	 */
	public byte[] getReceivingPacket() throws InterruptedException {
		byte[] tempPacket ;
		tempPacket = (byte[]) this.receivingQueue.take();
		return tempPacket;
	}
	
	/**
	 * Builds a datagramPacket from the bytes, port and address given and adds it to the sendingqueue.
	 * @param receiver
	 * @param receiverPort
	 * @param packet
	 */
	public void putSendingData(InetAddress receiver, int receiverPort, byte[] packet) {
		DatagramPacket packetToSend = new DatagramPacket(packet, packet.length, receiver, receiverPort);
		try {
			this.receivingQueue.put(packetToSend);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
