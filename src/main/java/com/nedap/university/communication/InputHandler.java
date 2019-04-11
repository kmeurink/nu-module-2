package com.nedap.university.communication;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nedap.university.utilities.FlagBytes;
import com.nedap.university.utilities.InputCommands;

/**
 * Deals with the packet header to identify the purpose and contents of a packet.
 * @author kester.meurink
 *
 */
public class InputHandler {//TODO perhaps better as a method, but is used by both server and client.
	//Named Constants:
	private PacketBuilder inputPacket;
	private PacketBuilder outputPacket;
	private int headerSize = 16; //TODO make changeable eventually
	private int packetSize = 1024;//TODO make changeable eventually
    private InetAddress BROADCAST;
    private InetAddress server;
    private int serverPort;
    private InputCommands commands;
    private PacketSender packetSender;
    private DatagramSocket socket;
    private FileListCompiler fileNameHandler;
    
	//Constructors:
	public InputHandler(DatagramSocket socket, InetAddress server, int serverPort) throws UnknownHostException {
		this.socket = socket;
		this.server = server;
		this.serverPort = serverPort;
		inputPacket = new PacketBuilder(headerSize, packetSize);
		outputPacket= new PacketBuilder(headerSize, packetSize);
		this.fileNameHandler = new FileListCompiler();
		this.BROADCAST = InetAddress.getByName("255.255.255.255");
		commands = new InputCommands();
		packetSender = new PacketSender(this.socket, this.server, this.serverPort);
		packetSender.start();
	}
	
	//Queries:
	
	
	
	//Commands:
	
	//PacketInput commands:
	
	public void bindAddress(InetAddress server) {
		packetSender.setAddress(server);
		this.server = server;
	}
	
	/**
	 * Reads out the contents of the packet and determines what to do.
	 * @param packet
	 */
	public void PacketInputSort(byte[] packet, InetAddress addr, int port) { //TODO determine if the current setup is correct.
		inputPacket.clearData();
		inputPacket.clearHeader();
		System.out.println("Starting flag selection.");
		List<byte[]> dataList = new ArrayList<byte[]>();
		byte[] data;
		inputPacket.setPacket(packet);
		if (Arrays.equals(inputPacket.calculateCheckSum(packet), inputPacket.getCheckSum())) {
			
		
		byte command = inputPacket.getFlags(); //TODO change to bytes
		switch(command) { //TODO add actions
		//List function options:
		case (byte) 33: //SYN/LIST
			System.out.println("Command tree: SYN/LIST");
			dataList = commands.listRequest();
			packetSender.addToQueue(dataList);
			break;
		case (byte) 35: //SYN/LIST/ACK
			System.out.println("Command tree: SYN/LIST/ACK");
			//TODO collect data
			this.fileNameHandler.addToList(inputPacket.getData());
			data = commands.listAcknowledgement();
			packetSender.addToQueue(data);
			break;
		case (byte) 39: //SYN/LIST/ACK/FIN
			System.out.println("Command tree: SYN/LIST/ACK/FIN");
    		//TODO collect last piece of data and add together to print out list given
			this.fileNameHandler.addToList(inputPacket.getData());
			this.fileNameHandler.compileList();;
			data = commands.listFinalAcknowledgement();
			packetSender.addToQueue(data);
			break;
		case (byte) 34: //LIST/ACK
			System.out.println("Command tree: LIST/ACK");
			commands.listReceivedAcknowledgement();

			break;
		//Pause function options:
		case (byte) 65: //PAUSE/SYN
			commands.pauseSynchronization();
			break;
		case (byte) 67: //PAUSE/SYN/ACK
			commands.pauseSynchronizationAcknowledgement();
			break;
		case (byte) 66: //PAUSE/ACK
			commands.pauseAcknowledgement();
			break;
		//Download function options:
		case (byte) 17: //SYN/DOWNLOAD
			commands.downloadSynchronization();
			break;
		case (byte) 19: //SYN/DOWNLOAD/ACK
			commands.downloadSynchronizationAcknowledgement();
			break;
		case (byte) 18: //ACK/DOWNLOAD
			commands.downloadAcknowledgement();
			break;
		case (byte) 16: //DOWNLOAD
			commands.download();
			break;
		case (byte) 20: //FIN/DOWNLOAD
			commands.downloadFinish();
			break;
		case (byte) 22: //FIN/DOWNLOAD/ACK
		commands.downloadFinishAcknowledgment();
		break;

		//Upload function options:
		case (byte) 9: //SYN/UPLOAD
			commands.uploadSynchronization();
			break;
		case (byte) 11: //SYN/UPLOAD/ACK
			commands.uploadSynchronizationAcknowledgement();
			break;
		case (byte) 8: //UPLOAD
			commands.upload();
			break;
		case (byte) 10: //UPLOAD/ACK
			commands.uploadAcknowledgement();
			break;
		case (byte) 12: //FIN/UPLOAD
			commands.uploadFinish();
			break;
		case (byte) 14: //FIN/UPLOAD/ACK
			commands.uploadFinishAcknowledgement();
			break;
		//Stop function options:
		case (byte) -127: //STARTSTOP/SYN
			commands.stopSynchronization();
			break;
		case (byte) -126: //STARTSTOP/ACK
			commands.stopAcknowledgement();
			break;
		case (byte) 0: //no flags set, only used for a broadcast.TODO is this useful?
			
			break;
		}
		}
		
	}
	
	//userInput commands:
	/**
	 * Sends list request to server.
	 */
	public void getList() {//TODO perhaps move to other class?
		this.outputPacket.setFlags(FlagBytes.SYNLIST);
		byte[] outputData = this.outputPacket.getPacket();
		packetSender.addToQueue(outputData);

	}
	
	/**
	 * Inner class to handle the composition of the file names list.
	 * @author kester.meurink
	 *
	 */
	private class FileListCompiler {
       private List<byte[]> nameListByte = new ArrayList<byte[]>();
       
       public void addToList(byte[] fileNames) {
    	   this.nameListByte.add(fileNames);
       }
       
       public void compileList() {
           	String concat =",";
	        //This is assuming all bytes have been received, so all bytes must be collected first before translating back to string. listFinalAcknowledgement method
	        int byteLengthNames = 0;
	        for (byte[] i: nameListByte) {
	        	byteLengthNames += i.length;
	        }
	        byte[] reconvertedNamesList = new byte[byteLengthNames];
	        int pointerIndex = 0;
	        for (byte[] i: nameListByte) {
	        	for (int j = 0; j < i.length; j++) {
	        		reconvertedNamesList[pointerIndex] = i[j];
	        		pointerIndex++;
	        	}
	        }

	        //Now convert back from byte array to the strings using the known concatenation symbol. listFinalAcknowledgement method
	        String receivedNames= "";
	        String[] allNamesReceived;
	        receivedNames = new String(reconvertedNamesList);
	        allNamesReceived =receivedNames.split(concat);
	        for (String i : allNamesReceived) {
	            System.out.println(i);
	        }
       }
	}

}
