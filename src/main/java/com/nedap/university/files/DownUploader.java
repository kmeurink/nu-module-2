package com.nedap.university.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import com.nedap.university.utilities.InputCommands;

/**
 * Takes care of the downloading of a file, keeping track of its progress.
 * @author kester.meurink
 *
 */
//TODO i have combined uploader and downloader, as they should have the same methods.
public class DownUploader {//TODO work out how it should work. And make it multithreaded?
	
	//Named constants:
	private String fileName;
	private short fileNumber;
	private int fileSize;
	private int currentPointer = 0;
	private List<byte[]> fileData; //TODO is list the best container?
	private String filePath= ""; //TODO determine how to choose this.
	private File file;
	private int standardFormat = 1002;
	//private byte[] packetDataSize = new byte[standardFormat]; //TODO be able to set this size.
	private int lastPacketSize = 0;
	private boolean download = true;
	private int seqNumber;
	private int ackNumber;
	private long checkSum;
	private String directory = "testFiles/"; //TODO change for the pi
	private File fileDirectory = new File(directory);
	private File currentFile;
	private FileInputStream fis;
	private FileOutputStream fos;
	private boolean lastPart = false;
	private boolean paused = false;
	//Constructors:
	public DownUploader() { //TODO make multithreaded?
		this.fileData = new ArrayList<byte[]>();
	}
	
	
	//Queries:
	
	/**
	 * Returns the filenumber for this file.
	 * @return
	 */
	public short getFileNumber() {
		return this.fileNumber;
	}
	
	/**
	 * Returns the name of the downloading file.
	 * @return
	 */
	public String getName() {
		return this.fileName; //TODO has to include datatype
	}
	
	/**
	 * Returns the size of the downloading file in bytes.
	 * @return
	 */
	public int getSize() {
		return this.fileSize;
	}
	
	/**
	 * Returns the status of this specific DownUploader.
	 * @return
	 */
	public boolean getStatus() {
		return this.download;
	}
	
	public boolean getPaused() {
		return this.paused;
	}
	
	/**
	 * Returns the checksum of the sent file.
	 * @return
	 */
	public long getCRC() {
		return this.checkSum;
	}
	
	//Commands:
	
	/**
	 * Sets the filenumber for this file.
	 * @return
	 */
	public void setFileNumber(short number) {
		this.fileNumber = number;
	}
	
	/**
	 * Set whether the file is a download or upload.
	 * @param i
	 */
	public void setStatus (boolean i) {
		if (i) {
			this.download = true; //It is a download.
		} else {
			this.download = false; //It is an upload.
		}
	}
	
	/**
	 * Determines the size of the filedata that is currently present.
	 * @return
	 */
	public int determineSize() {
		int size = 0;
		for (byte[] i : this.fileData) {
			size += i.length;
		}
		return size;
	}
	
	/**
	 * Sets the size of the data packet that is expected.
	 * @param size
	 */
	public void setSize(int size) {
		this.fileSize = size;
	}
	
	/**
	 * Changes the paused state of the down or upload.
	 */
	private void setPaused() {
		if (this.paused == false) {
			this.paused = true;
		} else {
			this.paused = false;
		}
	}
	
	/**
	 * Sets the name of the file including data type.
	 * @param name
	 */
	public void setName(String name, boolean write) {
		this.fileName = name;
		this.currentFile = new File(fileDirectory, fileName);
		try {
			if (write) {
					fos = new FileOutputStream(currentFile);
				} else {
				fis = new FileInputStream(currentFile);
				this.setSize(fis.available());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a datablock to the filedata.
	 * @param data
	 */
	public void addData(byte[] data) {
		this.fileData.add(data);
	}
	
	/**
	 * Wraps up the byte arrays of the file into a single array and writes it to an actual file.
	 */
	public void finishFile() { //Maybe make threaded to speed up sending/receiving?
		byte[] finalFile = new byte[this.determineSize()];
		int count = 0;
		for (byte[] i : this.fileData) {
			for (byte j : i) {
				finalFile[count] = j;
				count++;
			}
		}
		//TODO get file checksum given by server or client.
		long receivedChecksum = 0;
		//if (calculateFileChecksum(finalFile) == receivedChecksum) {
			
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			for(byte i : finalFile) {
				fos.write(i);
			}
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) { //TODO handle error
			e.printStackTrace();
		} catch (IOException e) { //TODO handle error
			e.printStackTrace();
		}
	//} else {
		System.out.println("File was corrupted, please try again.");//TODO further implement a feedback system.
	//}
		
	}
	
	/**
	 * Writes a part of the file to the actual file location.
	 */
	public void writeFilePart(byte[] data) {
		try {
			byte[] tempStorage = data;
			for (int i = 0; i < tempStorage.length; i++) {
				if(this.currentPointer < this.getSize()) {
					fos.write(data[i]);
					//System.out.println("Pointer is at: "+ currentPointer + " filesize is: " + this.getSize() );
					this.currentPointer++;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** @deprecated
	 * Reads out the file that is to be uploaded, and stores it as a list of byte arrays.
	 */
	public void readOutFile() { //name includes full directory
		try { //TODO tidy up
			//File readFile = new File(name);
			this.fileData.clear(); //Make sure no data is currently present.
			FileInputStream fis = new FileInputStream(currentFile);
			int content;
			this.fileSize = fis.available();
			//while ((content = fis.read(packetDataSize)) != -1) {
			//	this.fileData.add(packetDataSize.clone());
				//System.out.println(new String(packetDataSize));
				//System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			//	int templastPacketSize = (packetDataSize.length < fis.available()) ? standardFormat :fis.available();
				//lastPacketSize = (templastPacketSize == 0) ? lastPacketSize : templastPacketSize;
			//}
			//System.out.println("Last packet was: " + lastPacketSize);
			if (this.lastPacketSize != standardFormat) {
				byte[] resizedDataBlock = new byte[this.lastPacketSize];
				byte[] temp = this.fileData.get(this.fileData.size() - 1);
				for (int i = 0; i < lastPacketSize; i++) {
					resizedDataBlock[i] = temp[i];
				}
				this.fileData.set(this.fileData.size() - 1, resizedDataBlock);
			}
			//for (byte[] i : this.fileData) {
			//	System.out.println(new String(i));
			//}
			//System.out.println("----------------------------------------------------------------------");

		}catch (FileNotFoundException e) { //TODO handle error
			e.printStackTrace();
		} catch (IOException e) { //TODO handle error
			e.printStackTrace();
		}
	}
	
	/** 
	 * Reads out a part of the file that is to be uploaded.
	 */
	public byte[] readOutFilePart() {
		byte[] filePart = new byte[standardFormat];
		try {
			//FileInputStream fis = new FileInputStream(currentFile);
			int content;
			int PacketSize = (filePart.length < fis.available()) ? standardFormat : fis.available();
			filePart = new byte[PacketSize];
			if ((content = fis.read(filePart)) == -1) {
				filePart = null;
			}
			} catch (IOException e) { //TODO handle error
			e.printStackTrace();
		}
		return filePart;
	}
	
	/**
	 * Method to check if there is more than one part of the file to send.
	 * @return
	 */
	public boolean checkIfLastPart() {
		try {
			if (fis.available() <= standardFormat) {
				lastPart = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.lastPart;
	}
	/**
	 * Sets the data contents for a DOWNLOAD/SYN/ACK packet
	 * @return
	 */
	public byte[] downloadInitialization() {
		byte[] init = new byte[12];
		byte[] nameLength = InputCommands.intToBytes(this.getName().length());
		byte[] name = this.getName().getBytes();
		byte[] dataLength = InputCommands.intToBytes(this.getSize());
		byte[] crc = InputCommands.longToBytes(this.calculateFileChecksum());
		init = InputCommands.concat(nameLength, name, dataLength, crc);
		return init;
	}
	
	/**
	 * Sets the contents for an UPLOAD/SYN packet.
	 * @return
	 */
	public byte[] uploadInitialization() {
		byte[] init = new byte[12];
		
		return init;
	}
	
	/**
	 * Should send out all the byte arrays in the list, allowing for them to be uploaded.
	 */
	public List<byte[]> sendData() { //TODO not sure if i want it like this, maybe just allow access to the list for the filehandler to access it?
		return this.fileData;
	}
	
	public byte[] sendDataSingle(int dataBlock) { //TODO which is better for the filehandler?
		return this.fileData.get(dataBlock);
	}
	
	//TODO determine what class has to do what with keeping track of the properties.
	//TODO currently packetbuilder also has these methods.
	/**
	 * Sets the sequence number of this specific download/upload.
	 */
	public void setSeqNumber(int seq) {
		this.seqNumber = seq;
	}
	
	/**
	 * Sets the acknowledgement number of this specific download/upload.
	 */
	public void setAckNumber(int ack) {
		this.ackNumber = ack;
	}
	
	/**
	 * Sets the checksum for this file.
	 * @param crc
	 */
	public void setFileCRC(long crc) {
		this.checkSum = crc;
	}
	/**
	 * Calculates the checksum for a full file of data.
	 * @param file
	 * @return
	 */
	public long calculateFileChecksum() {
		CRC32 checkSum = new CRC32();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(currentFile);
			int cnt;
			while ((cnt = inputStream.read()) != -1) {
				checkSum.update(cnt);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return checkSum.getValue();
	}

	/**
	 * Sets the file directory to the selected location.
	 * @param directory
	 */
	public void setDirectory(File directory) {
		this.fileDirectory = directory;		
	}

}
