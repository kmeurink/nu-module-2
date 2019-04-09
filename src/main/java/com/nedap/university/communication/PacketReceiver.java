package com.nedap.university.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
/**
 * Receives the packets from the server or client.
 * @author kester.meurink
 *
 */
public class PacketReceiver {//TODO perhaps better as a method, but is used by both server and client. Also has to be given the socket to work.
	
	//Named Constants:
	private DatagramSocket socket;
	private int packetSize = 1024;//TODO remove eventually
	private int receiverPort;
	private InetAddress receiverAddress;
	//private byte[] packetData;
	private boolean active = true;
	
	//Constructors:
	
	public PacketReceiver(DatagramSocket socket) {
		this.socket = socket;
		//this.packetData = new byte[512];
	}
		
	//Queries:
	/**
	 * Returns the port of the sender of the last packet.
	 * @return
	 */
	public int getReceiverPort() {
		return this.receiverPort;
	}
	
	
	/**
	 * Returns the address of the sender of the last packet.
	 * @return
	 */
	public InetAddress getReceiverAddress() {
		return this.receiverAddress;
	}
	
	//Commands:
	  /**
     * Class to receive any packets sent to the socket and return them.
     * @throws IOException
     */
    public byte[] receivePacket() throws IOException { //TODO rewrite method, is messy
    	byte[] packetData;
    	//while (active) { //TODO figure out how to keep receiving, here might not be the best place for the loop.
        	//Receive packet from client.
            DatagramPacket request = new DatagramPacket(new byte[packetSize], packetSize); //TODO the large packetsize might cause problems for smaller packets when compiling the data together. Need to distinguish useful data.
            socket.receive(request);
            //Get address and port of client from the request.
            this.receiverAddress = request.getAddress();
            this.receiverPort = request.getPort();
            int dataLength = request.getLength(); //Determine the actual amount of data
            packetData = new byte[dataLength];
            byte[] totalPacket = new byte[packetSize];
            totalPacket = request.getData();
            for(int i = 0; i < dataLength; i++) { //Loop through the packet and only assign the bytes containing data.
            	packetData[i] = totalPacket[i];
            }

            //TODO add perhaps a notify that notifies the handler to do something with the received packet.
            return packetData;
        //}
    }
    
    /**
     * Starts or stops the receiving depending on its current state.
     */
    public void startStopReceiving() {
    	if (this.active == false) {
    		this.active = true;
    		System.out.println("Starting receiver");
    	} else {
    		this.active = false;
    		System.out.println("Stopping receiver");
    	}
    }
    
    
}
