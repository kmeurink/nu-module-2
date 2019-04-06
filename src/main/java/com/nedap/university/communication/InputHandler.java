package com.nedap.university.communication;

/**
 * Deals with the packet header to identify the purpose and contents of a packet.
 * @author kester.meurink
 *
 */
public class InputHandler {//TODO perhaps better as a method, but is used by both server and client.
	//Named Constants:
	private PacketBuilder inputPacket;
	private int dataSize = 499; //TODO make changeable eventually
	private int packetSize = 512;//TODO make changeable eventually
	//Possible flag combinations: TODO check if sufficient
	private final static int SYN = (int)Integer.valueOf("00000001", 2);;
	private final static int ACK = (int)Integer.valueOf("00000010", 2);;
	private final static int FIN = (int)Integer.valueOf("00000100", 2);;
	private final static int UPLOAD = (byte)(int)Integer.valueOf("00001000", 2);;
	private final static int DOWNLOAD = (byte)(int)Integer.valueOf("00010000", 2);;
	private final static int LIST = (byte)(int)Integer.valueOf("00100000", 2);;
	private final static int PAUSE = (byte)(int)Integer.valueOf("01000000", 2);;
	private final static int START_STOP = (byte)(int)Integer.valueOf("10000000", 2);
	
	
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
		}
	}
}
