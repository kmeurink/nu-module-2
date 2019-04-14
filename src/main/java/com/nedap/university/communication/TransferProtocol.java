package com.nedap.university.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.nedap.university.utilities.InputCommands;

/**
 * Class handles reliable transfer of packets between client and server.
 * @author kester.meurink
 *
 */
public class TransferProtocol extends Thread { //TODO trying to implement an alternating bit protocol, in which main calls this file which then can call the inputhandler. 

	//Named constants:
	static final int HEADERSIZE=22;   // number of header bytes in each packet
    static final int DATASIZE=1024;   // max. number of data bytes in each packet
    static final int TIMEOUTDELAY=500;   // time in ms before a packet is retransmitted.
    private int seqNum = 0;
    private byte[] lastPacketSent; //TODO place the last packet sent here.
    private DatagramSocket socket;
    private BlockingQueue<byte[]> sendingQueue;
    private List<byte[]> sentPacketList;
    private BlockingQueue<byte[]> receivingQueue;
    private List<byte[]> receivedPacketList;
    private InetAddress receiverAddress;
    private int receiverPort;
    private InputCommands commands;
    private FileListCompiler fileNameHandler;


    
    private int queueLength = 1000;
    private byte[] lastPacketReceived; //TODO place the last packet received here.
    private String[] currentAvailableFiles;

    private int tryCount = 0;
    private int packetCount = 0;
    private int packetsSent = 0;
    private int maxPackets = 1;
    
    private PacketBuilder receivedPacketAnalyzer;
    private PacketBuilder sentPacketAnalyzer;
    //DONE create a queue to which packets can be added that are received and the methods associated with it.
    
    //Constructors:
    public TransferProtocol(DatagramSocket socket) {
    	this.socket = socket;
    	this.sendingQueue = new ArrayBlockingQueue<byte[]>(queueLength);
    	this.receivingQueue = new ArrayBlockingQueue<byte[]>(queueLength);
    	this.sentPacketList = new ArrayList<byte[]>();
    	this.receivedPacketList = new ArrayList<byte[]>();
    	this.receivedPacketAnalyzer = new PacketBuilder(HEADERSIZE, DATASIZE);
    	this.sentPacketAnalyzer = new PacketBuilder(HEADERSIZE, DATASIZE);
    	this.commands = new InputCommands();
		this.fileNameHandler = new FileListCompiler();
    }
    
    public void run() {//TODO make it running all the time taking from the receiving queue and then sending.
    	while (true) {
    		//TODO should call sender and receiver.
    	}
    }
    
    public void sender() {
        System.out.println("Sending...");
    
        // read from the input file
        //TODO Check what has already been sent(check against window).
        //TODO If the window has been filled, in this case if one packet has been sent. Do not sent another packet.
        if (sentPacketList.size() < maxPackets) {
            //TODO If the receiver okays the received packet a new packet can be sent, this is taken from the sending queue.
        	byte[] packetToSend = this.sendingQueue.take();
        	socket.send(buildDatagram(this.receiverAddress, this.receiverPort, packetToSend));
        }
    	//TODO for this packet a new time out must be started which is also given the packet so that it is able to retransmit it.
        //TODO This sending queue is filled by the user functions of the client and by the reaction of the receiver to its received packet.

            //TODO schedule a timer, to ensure the packet is retransmitted if it never receives an ack.
            client.Utils.Timeout.SetTimeout(TIMEOUTDELAY, this, this.lastFrameSent);
    }
    
	/**
	 * Builds the datagram packet that is to be sent to the receiver.
	 * @param receiver
	 * @param receiverPort
	 */
	public DatagramPacket buildDatagram(InetAddress receiver, int receiverPort, byte[] packet) {
		DatagramPacket packetToSend = new DatagramPacket(packet, packet.length, receiver, receiverPort);
		return packetToSend;
	}
    
    public void TimeoutElapsed(Object tag) {
        int z=(Integer)tag;
        // handle expiration of the timeout:
        if (this.seqNum == z) {
        	//Retransmit packet and restart timer.
            getNetworkLayer().sendPacket(this.lastPacketSent);
            packetsSent++;
            client.Utils.Timeout.SetTimeout(TIMEOUTDELAY, this, z);
            System.out.println("Acknowledgement not received, tag = " + z);
        }
    }

    
    public void receiver() {
        System.out.println("Receiving...");
        //TODO here take from the received packet queue to which received packets can be put.
        try {
			this.lastPacketReceived = this.receivingQueue.take();
	        this.receivedPacketAnalyzer.setPacket(lastPacketReceived);
	        if(PacketFlagSelection()) {
	        	//TODO cancel timer and sent new packet
	        } //TODO otherwise nothing should happen, and the timer should run out.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //TODO then read out this packet using a packetBuilder
        //TODO use the format of the inputhandler or move its methods here to determine what to do with the packet.
        // The inputhandler must somehow return the result of the packet comparison, if it is an expected packet cancel the retransmit timer.
        // if it is not, then if it is an earlier packet an ack must also be sent, but this is mainly important for a sliding window expansion.
        // The expected packet must be processed and the packet resulting from it must be added to the send queue.
        PacketFlagSelection();
    }
    
    
    public void setAddress(InetAddress address) {
    	this.receiverAddress = address;
    }
    
    public void setPort(int port) {
    	this.receiverPort = port;
    }
    
    //packet header analysis methods:
    /**
	 * Reads out the contents of the packet and determines what to do.
	 * @param packet
	 */
	public boolean PacketFlagSelection() { //TODO determine if the current setup is correct.
		System.out.println("Starting flag selection.");
		boolean correctPacket = false;
		byte[] data;
		//System.out.println(Arrays.toString(inputPacket.calculateCheckSum(inputPacket.getCRCFile())));
		//System.out.println(Arrays.toString(inputPacket.getCheckSum()));
		if (Arrays.equals(this.receivedPacketAnalyzer.calculateCheckSum(this.receivedPacketAnalyzer.getCRCFile()), this.receivedPacketAnalyzer.getCheckSum())) { //TODO Check if this works, getCRCFile has not yet been tested.
			
		
		byte command = receivedPacketAnalyzer.getFlags();
		switch(command) { //TODO add actions
		//List function options:
		case (byte) 33: //SYN/LIST
			System.out.println("Command tree: SYN/LIST");
			commands.listRequest();
			data = commands.getListPart();
			if (data != null) {
				this.sendingQueue.add(data);
			}
			//System.out.println("Filename list size" + dataList.size());
			break;
		case (byte) 35: //SYN/LIST/ACK
			System.out.println("Command tree: SYN/LIST/ACK");
			//TODO collect data
			this.fileNameHandler.addToList(this.receivedPacketAnalyzer.getData());
			data = commands.listAcknowledgement();
			this.sendingQueue.add(data);
			break;
		case (byte) 39: //SYN/LIST/ACK/FIN
			System.out.println("Command tree: SYN/LIST/ACK/FIN");
    		//TODO collect last piece of data and add together to print out list given
			this.fileNameHandler.addToList(this.receivedPacketAnalyzer.getData());
			this.fileNameHandler.compileList();
			data = commands.listFinalAcknowledgement();
			this.sendingQueue.add(data);
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
		return correctPacket;
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
	
    
    //Queueing and list methods:
    
    /**
     * Method for other classes to add packets to the queue to send.
     * @param packet
     */
    public void addToSendingQueue(byte[] packet) {
    	this.sendingQueue.add(packet);
    }
    
    /**
     * Method for the socket to add its received packets to the queue.
     * @param packet
     */
    public void addToReceivingQueue(byte[] packet, InetAddress addr, int port) {
    	this.receivingQueue.add(packet);
    }
}
