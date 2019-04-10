package com.nedap.university.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nedap.university.utilities.InputCommands;

/**
 * Deals with the packet header to identify the purpose and contents of a packet.
 * @author kester.meurink
 *
 */
public class InputHandler {//TODO perhaps better as a method, but is used by both server and client.
	//Named Constants:
	private PacketBuilder inputPacket;
	private int dataSize = 1011; //TODO make changeable eventually
	private int packetSize = 1024;//TODO make changeable eventually
    private InetAddress BROADCAST;
    private InputCommands commands;
	//Constructors:
	public InputHandler() throws UnknownHostException {
		inputPacket = new PacketBuilder(dataSize, packetSize);
		this.BROADCAST = InetAddress.getByName("255.255.255.255");
		commands = new InputCommands();
	}
	
	//Queries:
	
	
	
	//Commands:
	
	//PacketInput commands:
	
	/**
	 * Reads out the contents of the packet and determines what to do.
	 * @param packet
	 */
	public void PacketInputSort(byte[] packet, InetAddress addr, int port) { //TODO determine if the current setup is correct.
		List<byte[]> dataList = new ArrayList<byte[]>();
		inputPacket.setPacket(packet);
		if (Arrays.equals(inputPacket.calculateCheckSum(packet), inputPacket.getCheckSum())) {
			
		
		int command = (int) inputPacket.getFlags();
		switch(command) { //TODO add actions
		//List function options:
		case (byte) 33: //SYN/LIST
			dataList = commands.listRequest();
			break;
		case (byte) 35: //SYN/LIST/ACK
			
			break;
		case (byte) 39: //SYN/LIST/ACK/FIN
			
			break;
		case (byte) 34: //LIST/ACK
			
			break;
		//Pause function options:
		case (byte) 65: //PAUSE/SYN
			
			break;
		case (byte) 67: //PAUSE/SYN/ACK
			
			break;
		case (byte) 66: //PAUSE/ACK
			
			break;
		//Download function options:
		case (byte) 17: //SYN/DOWNLOAD
			
			break;
		case (byte) 19: //SYN/DOWNLOAD/ACK
			
			break;
		case (byte) 18: //ACK/DOWNLOAD
			
			break;
		case (byte) 16: //DOWNLOAD
			
			break;
		case (byte) 20: //FIN/DOWNLOAD
			
			break;
		//Upload function options:
		case (byte) 9: //SYN/UPLOAD
			
			break;
		case (byte) 11: //SYN/UPLOAD/ACK
			
			break;
		case (byte) 8: //UPLOAD
			
			break;
		case (byte) 10: //UPLOAD/ACK
			
			break;
		case (byte) 12: //FIN/UPLOAD
			
			break;
		case (byte) 14: //FIN/UPLOAD/ACK
			
			break;
		//Stop function options:
		case (byte) -127: //STARTSTOP/SYN
			
			break;
		case (byte) -126: //STARTSTOP/ACK
			
			break;
		case (byte) 0: //no flags set, only used for a broadcast.TODO is this useful?
			
			break;
		}
		}
		
	}
}
