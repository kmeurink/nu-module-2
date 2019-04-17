package com.nedap.university.communication;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.nedap.university.files.DownUploaderHandler;
import com.nedap.university.utilities.FlagBytes;
import com.nedap.university.utilities.InputCommands;

/**
 * Class handles reliable transfer of packets between client and server.
 * Currently implements a stop and wait arq protocol combined with a case switch for flag recognition.
 * @author kester.meurink
 *
 */
public class TransferProtocol extends Thread { //TODO current implementation is not fully reliable. Multiple actions at the same time are not possible and larger files also cause issues.

	//Named constants:
	static final int HEADERSIZE=22;   // number of header bytes in each packet
    static final int DATASIZE=1024;   // max. number of data bytes in each packet
    static final int TIMEOUTDELAY=500;   // time in ms before a packet is retransmitted.
    private int seqNum = 0;
    private int ackNum = 0;
    private byte[] lastPacketSent;
    private byte[] lastPacketAcked;
    private DatagramSocket socket;
    private BlockingQueue<byte[]> sendingQueue;
    private List<byte[]> sentPacketList;
    private BlockingQueue<byte[]> receivingQueue;
    private List<byte[]> receivedPacketList;
    private InetAddress receiverAddress;
    private int receiverPort;
    private InputCommands commands;
    private FileListCompiler fileNameHandler;
    private Timer packetTimer;
    private TimerTask timeoutTask;
    private Map<Short, Map<Integer, TimerTask>> timeOutList;
    private boolean packetTimerset = false;
    private int queueLength = 1000;
    private long retransmissionTime = 5000L;
    private byte[] lastPacketReceived;
    private String[] currentAvailableFiles;
    private DownUploaderHandler loadList;
    private boolean download = true;
    private boolean upload = false;
    private long packetLost = 0;
    private long packetsSent = 0;
    private long packetsReceived = 0;
    private int maxPackets = 1;
    private long packetSendTime;
    private long packetReceiveTime;
    private List<Long> transferTimes;
    private PacketBuilder receivedPacketAnalyzer;
    private PacketBuilder sentPacketAnalyzer;
    
    //Constructors:
    public TransferProtocol(DatagramSocket socket, File directory) {
    	this.timeOutList = new HashMap<>();
    	this.transferTimes = new ArrayList<Long>();
    	this.socket = socket;
    	this.sendingQueue = new ArrayBlockingQueue<byte[]>(queueLength);
    	this.receivingQueue = new ArrayBlockingQueue<byte[]>(queueLength);
    	this.sentPacketList = new ArrayList<byte[]>();
    	this.receivedPacketList = new ArrayList<byte[]>();
    	this.receivedPacketAnalyzer = new PacketBuilder(HEADERSIZE, DATASIZE);
    	this.sentPacketAnalyzer = new PacketBuilder(HEADERSIZE, DATASIZE);
    	this.commands = new InputCommands(directory);
		this.fileNameHandler = new FileListCompiler();
		this.packetTimer = new Timer();
		this.loadList = new DownUploaderHandler(directory);
    }
    
    public void run() {
    	while (true) {
    		sender();
    		receiver();
    	}
    }
    
    public void sender() {//TODO create better suiting name
        //System.out.println("Sending...");
        //TODO Check what has already been sent(check against window).
        //TODO If the window has been filled, in this case if one packet has been sent. Do not sent another packet.
        if (sentPacketList.size() < maxPackets) {
        	byte[] packetToSend;
			try {
				if (!this.sendingQueue.isEmpty()) {
					packetToSend = this.sendingQueue.take();
		        	this.lastPacketSent = packetToSend.clone();
		        	this.sentPacketAnalyzer.setPacket(lastPacketSent);
		        	socket.send(buildDatagram(this.receiverAddress, this.receiverPort, packetToSend));
		        	this.packetSendTime = System.nanoTime();
		        	this.packetsSent++;
		        	//TODO for this packet a new time out must be started which is also given the packet so that it is able to retransmit it.
		            //TODO This sending queue is filled by the user functions of the client and by the reaction of the receiver to its received packet.

		            //TODO schedule a timer, to ensure the packet is retransmitted if it never receives an ack. But not if it is an ack itself.
		        	if(sendPacketTimerAllowed()) {
		        		timeoutTask = new PacketTimeout(lastPacketSent, this.sentPacketAnalyzer.getFileNumber(), this.sentPacketAnalyzer.getSeqNumber(), 0);
			        	this.packetTimer.schedule(timeoutTask, retransmissionTime);
			        	//packetTimerset = true;
		        	}
				} else {
					//Thread.sleep(500);//TODO remove
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
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
        
    public void receiver() { //TODO create better suiting name
        //System.out.println("Receiving...");
        try {
        	byte[] packetReceived;
        	if (!this.receivingQueue.isEmpty()) {
    			packetReceived = this.receivingQueue.take();
    			this.lastPacketReceived = packetReceived.clone();
    	        //TODO then read out this packet using a packetBuilder
    			//TODO also check if it is the same packet as the last acked, this then means the ack was not received and a old one has to be re-sent.
    	        this.receivedPacketAnalyzer.setPacket(packetReceived);
    	        //TODO use the format of the input handler or move its methods here to determine what to do with the packet.
    	        // The input handler must somehow return the result of the packet comparison, if it is an expected packet cancel the retransmit timer.
    	        // if it is not, then if it is an earlier packet an ack must also be sent, but this is mainly important for a sliding window expansion.
    	        // The expected packet must be processed and the packet resulting from it must be added to the send queue.
    	        this.packetsReceived++;
    	        if(Arrays.equals(this.lastPacketReceived, this.lastPacketAcked)) {
    	        	this.sendingQueue.add(this.lastPacketSent);
    	        } else {
        	        if(PacketFlagSelection()) {
        	        	this.packetReceiveTime = System.nanoTime();
        	        	this.transferTimes.add((this.packetReceiveTime - this.packetSendTime));
        	        	//TODO if a timer has been set, cancel timer and allow for a new packet to be sent.
    		        	if(timeOutList.containsKey(this.receivedPacketAnalyzer.getFileNumber()) && timeOutList.get(this.receivedPacketAnalyzer.getFileNumber()).containsKey(this.receivedPacketAnalyzer.getAckNumber() - 1)) {
    		        	//	timeoutTask = new PacketTimeout(lastPacketSent, this.sentPacketAnalyzer.getFileNumber(), this.sentPacketAnalyzer.getSeqNumber());
    		        		Map <Integer, TimerTask> fileMap = this.timeOutList.get(this.receivedPacketAnalyzer.getFileNumber());
    		        		TimerTask task = fileMap.get(this.receivedPacketAnalyzer.getAckNumber() - 1);
    		        		task.cancel();
    	    	        	fileMap.remove(this.receivedPacketAnalyzer.getAckNumber() - 1);
    	    	        	if (fileMap.isEmpty()) {
    	    	        		this.timeOutList.remove(this.receivedPacketAnalyzer.getFileNumber());
    	    	        	}
    		        	}
        	        	this.sentPacketList.remove(this.lastPacketSent);
        	        	this.lastPacketAcked = this.lastPacketReceived.clone();
        	        }
    	        }
        	} else {
        		//Thread.sleep(500); //TODO remove
        	}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public void setAddress(InetAddress address) {
    	this.receiverAddress = address;
    }
    
    public void setPort(int port) {
    	this.receiverPort = port;
    }
    
    /**
     * Method to calculate the statistics of the file transfers.
     * @return
     */
    public String calculateStatistics() {
    	String statistics = "";
    	double maxSpeed = 0;
    	double avgSpeed = 0;
    	int count = 0;
    	for (int i = 0; i < this.transferTimes.size(); i++) {
    		double speedCheck = (double) ((double) DATASIZE/((double) this.transferTimes.get(i)/ (double) 1000000000));
    		if (speedCheck > maxSpeed) {
    			maxSpeed = speedCheck;
    		}
    		if (count > 1) {
    			avgSpeed = (speedCheck + avgSpeed) / 2;
    		} else {
    			avgSpeed = speedCheck;
    		}
    		
    	}
    	long packetAmountLost = this.packetLost;
    	long packetAmountSent =this.packetsSent;
    	long packetAmountReceived = this.packetsReceived;
    	statistics += "Average file speed: ";
    	statistics += avgSpeed;
    	statistics += " bytes/second. Maximal file speed: ";
    	statistics += maxSpeed;
    	statistics += " bytes/second. Amount of packets sent: ";
    	statistics += packetAmountSent;
    	statistics += " Amount of packets received: ";
    	statistics += packetAmountReceived;
    	statistics += " Amount of packets lost: ";
    	statistics += packetAmountLost;
    	return statistics;
    }
    
    /**
     * Determines if the packet sent requires a timer to be set for its retransmission.
     * @return
     */
    public boolean sendPacketTimerAllowed() {
    	boolean setTimer = false;
    	if (sentPacketAnalyzer.getFlags() != FlagBytes.LISTACK && sentPacketAnalyzer.getFlags() != FlagBytes.PAUACK && sentPacketAnalyzer.getFlags() != FlagBytes.ACKDOWN 
    			&& sentPacketAnalyzer.getFlags() != FlagBytes.UPACK && sentPacketAnalyzer.getFlags() != FlagBytes.FINDOWNACK && sentPacketAnalyzer.getFlags() != FlagBytes.FINUPACK 
    			&& sentPacketAnalyzer.getFlags() != FlagBytes.STOPACK) {
    		setTimer = true;
    	}
    	return setTimer;
    }
    
    //packet header analysis methods:
    /**
	 * Reads out the contents of the packet and determines what to do.
	 * @param packet
	 */
	public boolean PacketFlagSelection() { //TODO Layout of this case switch could be more organised, for example splitting it up in smaller sub methods. This does require splitting of flag bits.
		//System.out.println("Starting flag selection.");
		boolean correctPacket = false;
		byte[] data;
		//System.out.println(Arrays.toString(inputPacket.calculateCheckSum(inputPacket.getCRCFile())));
		//System.out.println(Arrays.toString(inputPacket.getCheckSum()));
		if (Arrays.equals(this.receivedPacketAnalyzer.calculateCheckSum(this.receivedPacketAnalyzer.getCRCFile()), this.receivedPacketAnalyzer.getCheckSum())) {
			
		
		byte command = receivedPacketAnalyzer.getFlags();
		switch(command) {
		//List function options:
		case (byte) 33: //SYN/LIST server side.
			//System.out.println("Command tree: SYN/LIST");
			if (this.receivedPacketAnalyzer.getAckNumber() == 0 && this.receivedPacketAnalyzer.getSeqNumber() == 0 && this.receivedPacketAnalyzer.getFileNumber() == 0) {
				correctPacket = true;
				commands.listRequest();
				data = commands.getListPart();
				if (data != null) {
					addToSendingQueue(data);
				}
			}
			//System.out.println("Filename list size" + dataList.size());
			break;
		case (byte) 35: //SYN/LIST/ACK client side.
			//System.out.println("Command tree: SYN/LIST/ACK");
			if (this.receivedPacketAnalyzer.getAckNumber() == (this.sentPacketAnalyzer.getSeqNumber() + 1) && this.receivedPacketAnalyzer.getSeqNumber() == 0 && this.receivedPacketAnalyzer.getFileNumber() == 0) {
				correctPacket = true;
				this.fileNameHandler.addToList(this.receivedPacketAnalyzer.getData());
				data = commands.listAcknowledgement(this.receivedPacketAnalyzer.getAckNumber());
				addToSendingQueue(data);
			}
			break;
		case (byte) 39: //SYN/LIST/ACK/FIN client side.
			//System.out.println("Command tree: SYN/LIST/ACK/FIN");
			if (this.receivedPacketAnalyzer.getAckNumber() == (this.sentPacketAnalyzer.getSeqNumber() + 1) && this.receivedPacketAnalyzer.getSeqNumber() == 0 && this.receivedPacketAnalyzer.getFileNumber() == 0) {
				correctPacket = true;
				this.fileNameHandler.addToList(this.receivedPacketAnalyzer.getData());
				this.fileNameHandler.compileList();
				data = commands.listFinalAcknowledgement(this.receivedPacketAnalyzer.getAckNumber());
				addToSendingQueue(data);
			}	

			break;
		case (byte) 34: //LIST/ACK server side.
			//System.out.println("Command tree: LIST/ACK");
			if (this.receivedPacketAnalyzer.getSeqNumber() == this.sentPacketAnalyzer.getAckNumber() && this.receivedPacketAnalyzer.getFileNumber() == 0) {
				correctPacket = true;
				commands.listReceivedAcknowledgement();
			}
			break;
		//Pause function options:
		case (byte) 65: //PAUSE/SYN
			System.out.println("Command tree: PAUSE/SYN");
			correctPacket = true;

			commands.pauseSynchronization();
			break;
		case (byte) 67: //PAUSE/SYN/ACK
			System.out.println("Command tree: PAUSE/SYN/ACK");
			correctPacket = true;

			commands.pauseSynchronizationAcknowledgement();
			break;
		case (byte) 66: //PAUSE/ACK
			System.out.println("Command tree: PAUSE/ACK");

			commands.pauseAcknowledgement();
			break;
		//Download function options:
		case (byte) 17: //SYN/DOWNLOAD server side
			//System.out.println("Command tree: SYN/DOWNLOAD");
			if (this.receivedPacketAnalyzer.getAckNumber() == 0 && this.receivedPacketAnalyzer.getSeqNumber() == 0 && this.loadList.checkFilePresence(download, this.receivedPacketAnalyzer.getFileNumber())) {
				correctPacket = true;
				byte[] nameLength = Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), 0, 4);
				int fileNameLength = commands.byteArrayToInt(nameLength);
				String fileName = new String(Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), 4, fileNameLength + 4));
				this.loadList.createFileload(fileName, this.receivedPacketAnalyzer.getFileNumber(), false, true);
				data = commands.downloadSynchronization(this.receivedPacketAnalyzer.getFileNumber(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).downloadInitialization());
				addToSendingQueue(data);
			}
			break;
		case (byte) 19: //SYN/DOWNLOAD/ACK client side
			//System.out.println("Command tree: SYN/DOWNLOAD/ACK");
			if (this.receivedPacketAnalyzer.getAckNumber() == 1) {
				correctPacket = true;
				byte[] nameLength = Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), 0, 4);
				int fileNameLength = commands.byteArrayToInt(nameLength);
				String fileName = new String(Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), 4, fileNameLength + 4));
				byte[] fileSize = Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), fileNameLength + 4, fileNameLength + 4 + 4);
				byte[] crc = Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), fileNameLength + 4 + 4, fileNameLength + 4 + 4 + 8);
				this.loadList.createFileload(fileName, this.receivedPacketAnalyzer.getFileNumber(), true, true);
				this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).setSize(InputCommands.byteArrayToInt(fileSize));
				this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).setFileCRC(InputCommands.byteArrayToLong(crc));
				data = commands.downloadSynchronizationAcknowledgement(this.receivedPacketAnalyzer.getFileNumber());
				addToSendingQueue(data);
			}
			break;
		case (byte) 18: //ACK/DOWNLOAD server side
			//System.out.println("Command tree: ACK/DOWNLOAD");
			//System.out.println("sent packet file number: " + this.sentPacketAnalyzer.getFileNumber() + " received packet file number: " + this.receivedPacketAnalyzer.getFileNumber());
			//System.out.println("sent packet seq number: " + this.sentPacketAnalyzer.getSeqNumber() + " received packet ack number: " + this.receivedPacketAnalyzer.getAckNumber());
			if (this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber() && (this.sentPacketAnalyzer.getSeqNumber() + 1) == this.receivedPacketAnalyzer.getAckNumber()) {
				correctPacket = true;
				data = commands.downloadAcknowledgement(this.receivedPacketAnalyzer.getAckNumber(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()));
				addToSendingQueue(data);
			}
			break;
		case (byte) 16: //DOWNLOAD client side
			//System.out.println("Command tree: DOWNLOAD");
			if (this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber() && this.receivedPacketAnalyzer.getSeqNumber() == this.sentPacketAnalyzer.getAckNumber()) {
				correctPacket = true;
				data = commands.download(this.receivedPacketAnalyzer.getSeqNumber(), this.receivedPacketAnalyzer.getData(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()));
				addToSendingQueue(data);
			}
			break;
		case (byte) 20: //FIN/DOWNLOAD client side
			//System.out.println("Command tree: FIN/DOWNLOAD");
			if (this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber() && this.receivedPacketAnalyzer.getSeqNumber() == this.sentPacketAnalyzer.getAckNumber()) {
				correctPacket = true;
				data = commands.downloadFinish(this.receivedPacketAnalyzer.getSeqNumber(), this.receivedPacketAnalyzer.getData(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()));
		    	this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).closeWrite();
				long checkSum = this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).calculateFileChecksum();
		    	if (checkSum == this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).getCRC()) {
		    		System.out.println("File downloaded succesfully.");
		    	} 
		    	else {
		    		System.out.println("Sorry the file was corrupted.");
		    	}
	    		this.loadList.removeDownUpload(this.receivedPacketAnalyzer.getFileNumber());
				addToSendingQueue(data);
			}
			break;
		case (byte) 22: //FIN/DOWNLOAD/ACK server side
			//System.out.println("Command tree: FIN/DOWNLOAD/ACK");
			if (this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber()) {
				correctPacket = true;
				this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).closeRead();
	    		this.loadList.removeDownUpload(this.receivedPacketAnalyzer.getFileNumber());
			}
			//commands.downloadFinishAcknowledgment();
			break;

		//Upload function options:
		case (byte) 9: //SYN/UPLOAD server side
			//System.out.println("Command tree: SYN/UPLOAD");
			if (this.receivedPacketAnalyzer.getAckNumber() == 0 && this.receivedPacketAnalyzer.getSeqNumber() == 0 && this.loadList.checkFilePresence(upload, this.receivedPacketAnalyzer.getFileNumber())) {
				correctPacket = true;
				byte[] nameLength = Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), 0, 4);
				int fileNameLength = commands.byteArrayToInt(nameLength);
				String fileName = new String(Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), 4, fileNameLength + 4));
				int size = InputCommands.byteArrayToInt(Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), fileNameLength + 4, fileNameLength + 4 + 4));
				long crc = InputCommands.byteArrayToLong(Arrays.copyOfRange(this.receivedPacketAnalyzer.getData(), fileNameLength + 8, fileNameLength + 16));
				this.loadList.createFileload(fileName, this.receivedPacketAnalyzer.getFileNumber(), true, false);
				this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).setFileCRC(crc);
				this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).setSize(size);
				data = commands.uploadSynchronization(this.receivedPacketAnalyzer.getFileNumber());
				addToSendingQueue(data);
			}
			break;
		case (byte) 11: //SYN/UPLOAD/ACK client side
			//System.out.println("Command tree: SYN/UPLOAD/ACK");
			if (this.receivedPacketAnalyzer.getAckNumber() == 1 && this.receivedPacketAnalyzer.getSeqNumber() == 0 && this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber()) {
				correctPacket = true;
				data = commands.uploadSynchronizationAcknowledgement(this.receivedPacketAnalyzer.getAckNumber(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()));
				addToSendingQueue(data);
			}			
			break;
		case (byte) 8: //UPLOAD server side
			//System.out.println("Command tree: UPLOAD");
			if (this.receivedPacketAnalyzer.getSeqNumber() == this.sentPacketAnalyzer.getAckNumber() && this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber()) {
				correctPacket = true;
				data = commands.upload(this.receivedPacketAnalyzer.getSeqNumber(), this.receivedPacketAnalyzer.getData(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()));
				addToSendingQueue(data);
			}
			break;
		case (byte) 10: //UPLOAD/ACK client side
			//System.out.println("Command tree: UPLOAD/ACK");
			if ((this.receivedPacketAnalyzer.getAckNumber() - 1) == this.sentPacketAnalyzer.getSeqNumber() && this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber()) {
				correctPacket = true;
				data = commands.uploadAcknowledgement(this.receivedPacketAnalyzer.getAckNumber(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()));
				addToSendingQueue(data);
			}

			break;
		case (byte) 12: //FIN/UPLOAD server side
			//System.out.println("Command tree: FIN/UPLOAD");
			if (this.receivedPacketAnalyzer.getSeqNumber() == this.sentPacketAnalyzer.getAckNumber() && this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber()) {
				correctPacket = true;
				data = commands.uploadFinish(this.receivedPacketAnalyzer.getSeqNumber(), this.receivedPacketAnalyzer.getData(), this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()));
		    	this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).closeWrite();
				long checkSum = this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).calculateFileChecksum();
		    	if (checkSum == this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).getCRC()) {
		    		System.out.println("File uploaded succesfully.");
		    	} 
		    	else {
		    		System.out.println("Sorry the file was corrupted.");
		    	}
	    		this.loadList.removeDownUpload(this.receivedPacketAnalyzer.getFileNumber());
				addToSendingQueue(data);
			}
			break;
		case (byte) 14: //FIN/UPLOAD/ACK client side
			//System.out.println("Command tree: FIN/UPLOAD/ACK");
			if (this.sentPacketAnalyzer.getFileNumber() == this.receivedPacketAnalyzer.getFileNumber()) {
				correctPacket = true;
				this.loadList.getDownUploads().get(this.receivedPacketAnalyzer.getFileNumber()).closeRead();
				this.loadList.removeDownUpload(this.receivedPacketAnalyzer.getFileNumber());
			}
			//commands.uploadFinishAcknowledgement();
			break;
		//Stop function options:
		case (byte) -127: //STARTSTOP/SYN
			System.out.println("Command tree: STARTSTOP/SYN");
			correctPacket = true;

			commands.stopSynchronization();
			break;
		case (byte) -126: //STARTSTOP/ACK
			System.out.println("Command tree: STARTSTOP/ACK");
			correctPacket = true;

			commands.stopAcknowledgement();
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
    	   //System.out.println("Adding files to list. " +  Thread.currentThread()); 
    	   this.nameListByte.add(fileNames);
       }
       
       public void compileList() {
    	    //System.out.println("Compiling list. " +  Thread.currentThread()); 
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
	 * Returns the list with all currently available files on the server.
	 * @return
	 */
	public String[] getFileNameList() {
		return this.currentAvailableFiles;
	}
	
	/**
	 * Returns the list with all downUploaders for use in the inputhandler.
	 * @return
	 */
	public DownUploaderHandler getFilelist() {
		return this.loadList;
	}
		
    //Queueing and list methods:
    
    /**
     * Method for other classes to add packets to the queue to send.
     * @param packet
     */
    public void addToSendingQueue(byte[] packet) {
    	//System.out.println("Packet added to sending queue.");
    	this.sendingQueue.add(packet);
    }
    
    /**
     * Method for the socket to add its received packets to the queue.
     * @param packet
     */
    public void addToReceivingQueue(byte[] packet) {//, InetAddress addr, int port
    	//System.out.println("Packet added to receiving queue.");
    	this.receivingQueue.add(packet);
    }
    
    
    /**
     * Returns a fileNumber that is not yet in use.
     * @return
     */
    public short getAvailableFileNumber() {
    	short fileNum =(short) 1;
    	boolean usedValue = true;
    	int maxNum = (int) (Math.pow(2, 16) / 2) - 1;
    	Random random = new Random();
    	while (usedValue) {
        	fileNum = (short) random.nextInt(maxNum);
        	if (!this.loadList.getDownUploads().containsKey(fileNum)) {
        		usedValue = false;
        	}
    	}
    	return fileNum;
    }
    
    //Timer methods:
    
    private class PacketTimeout extends TimerTask {
    	private byte[] packet;
    	private short fileNum;
    	private int seqNum;
    	private int retransmissionCount;
    	int maxRetransmissionCount = 10;
    	public PacketTimeout(byte[] packet, short fileNum, int seqNum, int retransmissionCount) {
    		this.packet = packet;
    		this.fileNum = fileNum;
    		this.seqNum = seqNum;
    		this.retransmissionCount = retransmissionCount;
    		Map<Integer, TimerTask> tempMap= new HashMap<>();
    		tempMap.put(seqNum, this);
    		timeOutList.put(fileNum, tempMap);
    	}

		@Override
		public void run() {
			System.out.println("Retransmitting packet. Filenumber: " + fileNum + " Seqnumber: " + seqNum);
			setTask();
			
		}
    	
		public void setTask() {
			try {
				packetLost++;
				timeOutList.get(fileNum).remove(seqNum);
				if (retransmissionCount < maxRetransmissionCount) {
					TimerTask reTask = new PacketTimeout(packet, fileNum, seqNum, retransmissionCount + 1);
					if (!sendingQueue.contains(packet)) {
						socket.send(buildDatagram(receiverAddress, receiverPort, packet));
					}				
		        	packetTimer.schedule(reTask, retransmissionTime);
				}
			} catch (IOException e) {
				// TODO handle error
				e.printStackTrace();
			}
			
		}
    }
    
}
