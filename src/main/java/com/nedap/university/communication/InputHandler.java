package com.nedap.university.communication;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.JList;

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
	private int headerSize = 22; //TODO make changeable eventually
	private int packetSize = 1024;//TODO make changeable eventually
    private InetAddress BROADCAST;
    private InetAddress server;
    private int serverPort;
    private InputCommands commands;
    private PacketSender packetSender;
    private DatagramSocket socket;
    private FileListCompiler fileNameHandler;
    private String[] currentAvailableFiles;
    
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
		//System.out.println(Arrays.toString(inputPacket.calculateCheckSum(inputPacket.getCRCFile())));
		//System.out.println(Arrays.toString(inputPacket.getCheckSum()));
		if (Arrays.equals(inputPacket.calculateCheckSum(inputPacket.getCRCFile()), inputPacket.getCheckSum())) { //TODO Check if this works, getCRCFile has not yet been tested.
			
		
		byte command = inputPacket.getFlags();
		switch(command) { //TODO add actions
		//List function options:
		case (byte) 33: //SYN/LIST
			System.out.println("Command tree: SYN/LIST");
			commands.listRequest();
			data = commands.getListPart();
			//System.out.println("Filename list size" + dataList.size());
			packetSender.addToQueue(data);
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
			this.fileNameHandler.compileList();
			data = commands.listFinalAcknowledgement();
			packetSender.addToQueue(data);
			break;
		case (byte) 34: //LIST/ACK
			System.out.println("Command tree: LIST/ACK");
			commands.listReceivedAcknowledgement();

			break;
		//Pause function options:
		case (byte) 65: //PAUSE/SYN
			System.out.println("Command tree: PAUSE/SYN");
			commands.pauseSynchronization();
			break;
		case (byte) 67: //PAUSE/SYN/ACK
			System.out.println("Command tree: PAUSE/SYN/ACK");

			commands.pauseSynchronizationAcknowledgement();
			break;
		case (byte) 66: //PAUSE/ACK
			System.out.println("Command tree: PAUSE/ACK");

			commands.pauseAcknowledgement();
			break;
		//Download function options:
		case (byte) 17: //SYN/DOWNLOAD
			System.out.println("Command tree: SYN/DOWNLOAD");

			commands.downloadSynchronization();
			break;
		case (byte) 19: //SYN/DOWNLOAD/ACK
			System.out.println("Command tree: SYN/DOWNLOAD/ACK");

			commands.downloadSynchronizationAcknowledgement();
			break;
		case (byte) 18: //ACK/DOWNLOAD
			System.out.println("Command tree: ACK/DOWNLOAD");

			commands.downloadAcknowledgement();
			break;
		case (byte) 16: //DOWNLOAD
			System.out.println("Command tree: DOWNLOAD");

			commands.download();
			break;
		case (byte) 20: //FIN/DOWNLOAD
			System.out.println("Command tree: FIN/DOWNLOAD");

			commands.downloadFinish();
			break;
		case (byte) 22: //FIN/DOWNLOAD/ACK
			System.out.println("Command tree: FIN/DOWNLOAD/ACK");

		commands.downloadFinishAcknowledgment();
		break;

		//Upload function options:
		case (byte) 9: //SYN/UPLOAD
			System.out.println("Command tree: SYN/UPLOAD");
			commands.uploadSynchronization();
			break;
		case (byte) 11: //SYN/UPLOAD/ACK
			System.out.println("Command tree: SYN/UPLOAD/ACK");

			commands.uploadSynchronizationAcknowledgement();
			break;
		case (byte) 8: //UPLOAD
			System.out.println("Command tree: UPLOAD");

			commands.upload();
			break;
		case (byte) 10: //UPLOAD/ACK
			System.out.println("Command tree: UPLOAD/ACK");

			commands.uploadAcknowledgement();
			break;
		case (byte) 12: //FIN/UPLOAD
			System.out.println("Command tree: FIN/UPLOAD");

			commands.uploadFinish();
			break;
		case (byte) 14: //FIN/UPLOAD/ACK
			System.out.println("Command tree: FIN/UPLOAD/ACK");

			commands.uploadFinishAcknowledgement();
			break;
		//Stop function options:
		case (byte) -127: //STARTSTOP/SYN
			System.out.println("Command tree: STARTSTOP/SYN");

			commands.stopSynchronization();
			break;
		case (byte) -126: //STARTSTOP/ACK
			System.out.println("Command tree: STARTSTOP/ACK");

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
		this.outputPacket.clearData();
		this.outputPacket.clearHeader();
		this.outputPacket.setFlags(FlagBytes.SYNLIST);
		this.outputPacket.setCheckSum(outputPacket.calculateCheckSum(outputPacket.getCRCFile()));
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
    	   System.out.println("Adding files to list. " +  Thread.currentThread()); //TODO for testing.
    	   this.nameListByte.add(fileNames);
       }
       
       public void compileList() {
    	   System.out.println("Compiling list. " +  Thread.currentThread()); //TODO for testing.
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
	        currentAvailableFiles = allNamesReceived;
	        for (String i : allNamesReceived) {
	            System.out.println(i);
	        }
	        nameListByte.clear();
       }
	}
	
	/**
	 * Sends download request to server, first showing the available files and querying the user for the filename.
	 */
	public void downloadFile(Scanner userIn) { //TODO not very optimized, needs simplification.
		System.out.println("Please type the name of the file you would like to download: ");
		getList();
		String fileName = userIn.nextLine();
		byte[] fileNameByteVersion = fileName.getBytes();
		int fileNameLength = fileName.length();
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(fileNameLength);
		byte[] fileNameLengthBytes = b.array();
		this.outputPacket.setData(concat(fileNameLengthBytes, fileNameByteVersion));
		this.outputPacket.setFlags(FlagBytes.SYNDOWN);
		this.outputPacket.setCheckSum(outputPacket.calculateCheckSum(outputPacket.getCRCFile()));
		byte[] outputData = this.outputPacket.getPacket();
		packetSender.addToQueue(outputData);
	}
	
	/**
	 * Sends upload request to server, first querying user to select the file to upload.
	 */
	public void uploadFile() {
		
	}

	
	private byte[] concat(byte[]...arrays)
	{
	    // Determine the length of the result array
	    int totalLength = 0;
	    for (int i = 0; i < arrays.length; i++)
	    {
	        totalLength += arrays[i].length;
	    }

	    // create the result array
	    byte[] result = new byte[totalLength];

	    // copy the source arrays into the result array
	    int currentIndex = 0;
	    for (int i = 0; i < arrays.length; i++)
	    {
	        System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
	        currentIndex += arrays[i].length;
	    }

	    return result;
	}
}
