package com.nedap.university.communication;

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
	
	//Constructors:
	public InputHandler() {
		inputPacket = new PacketBuilder(dataSize, packetSize) ;
	}
	
	//Queries:
	
	
	
	//Commands:
	
	//PacketInput commands:
	
	/**
	 * Reads out the contents of the packet and determines what to do.
	 * @param packet
	 */
	public void PacketInputHandler(byte[] packet) { //TODO determine if the current setup is correct.
		inputPacket.setPacket(packet);
		int command = (int) inputPacket.getFlags();
		switch(command) { //TODO add actions
		case (byte) 1: //SYN
			
			break;
		case (byte) 2: //ACK
			
			break;
		case (byte) 4: //FIN
			
			break;
		case (byte) 8: //UPLOAD
			
			break;
		case (byte) 16: //DOWNLOAD
			
			break;
		case (byte) 32: //LIST
			
			break;
		case (byte) 64: //PAUSE
			
			break;
		case (byte) -128: //START/STOP
			
			break;
		case (byte) 3: //SYN/ACK
			
			break;
		case (byte) 9: //SYN/UPLOAD
			
			break;
		case (byte) 17: //SYN/DOWNLOAD
			
			break;
		case (byte) 33: //SYN/LIST
					
			break;
		case (byte) 6: //ACK/FIN
			
			break;
		case (byte) 10: //ACK/UPLOAD
			
			break;
		case (byte) 18: //ACK/DOWNLOAD
			
			break;
		case (byte) 34: //ACK/LIST
			
			break;
		case (byte) 66: //ACK/PAUSE
			
			break;
		case (byte) -126: //ACK/START_STOP
			
			break;
		case (byte) 5: //SYN/FIN
			
			break;
		case (byte) 12: //FIN/UPLOAD
			
			break;
		case (byte) 20: //FIN/DOWNLOAD
			
			break;
		case (byte) 36: //FIN/LIST
			
			break;
		case (byte) 68: //FIN/PAUSE
			
			break;
		case (byte) -124: //FIN/START_STOp
			
			break;
		case (byte) 0: //no flags set, only used for a broadcast.
			
			break;
		
		}
		
	}
}
