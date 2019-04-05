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
	private int packetSize = 512;//TODO remove eventually
	private int receiverPort;
	private InetAddress receiverAddress;
	private byte[] packetData;
	
	//Constructors:
	public PacketReceiver(DatagramSocket socket) {
		this.socket = socket;
		this.packetData = new byte[512];
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
     * Class to receive any packets sent to the socket.
     * @throws IOException
     */
    public void receivePacket() throws IOException { //TODO rewrite method, is messy
        //while (true) {
        	//Receive packet from client.
            DatagramPacket request = new DatagramPacket(new byte[packetSize], packetSize);
            socket.receive(request);
            this.packetData = request.getData();
            for (byte i : packetData) {
                System.out.println(Byte.toString(i)); //TODO for testing.

            }
            
            //Get address and port of client from the request.
            this.receiverAddress = request.getAddress();
            this.receiverPort = request.getPort();
            //TODO add perhaps a notify that notifies the handler to do something with the received packet.
        //}
    }
    
    
}
