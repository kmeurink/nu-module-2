package com.nedap.university.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes care of the downloading of a file, keeping track of its progress.
 * @author kester.meurink
 *
 */
//TODO i have combined uploader and downloader, as they should have the same methods.
public class DownUploader {//TODO work out how it should work. And make it multithreaded?
	
	//Named constants:
	private String fileName;
	private int fileSize;
	private List<byte[]> fileData; //TODO is list the best container?
	private String filePath= ""; //TODO determine how to choose this.
	private File file;
	private byte[] packetDataSize = new byte[499]; //TODO be able to set this size.
	private int lastPacketSize = 0;
	
	//Constructors:
	public DownUploader() {
		this.fileData = new ArrayList<byte[]>();
	}
	
	
	//Queries:
	
	/**
	 * Returns the name of the downloading file.
	 * @return
	 */
	public String getName() {
		return this.fileName; //TODO has to include datatype
	}
	
	/**
	 * Returns the size of the downloading file.
	 * @return
	 */
	public int getSize() {
		return this.fileSize;
	}
	
	
	//Commands:
	
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
	 * Sets the name of the file including data type.
	 * @param name
	 */
	public void setName(String name) {
		this.fileName = name;
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
	public void finishFile() {
		byte[] finalFile = new byte[this.determineSize()];
		int count = 0;
		for (byte[] i : this.fileData) {
			for (byte j : i) {
				finalFile[count] = j;
				count++;
			}
		}
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
		
	}
	
	/**
	 * Reads out the file that is to be uploaded, and stores it as a list of byte arrays.
	 */
	public void readOutFile(String name) { //name includes full directory
		try { //TODO tidy up
			//File readFile = new File(name);
			this.fileData.clear(); //Make sure no data is currently present.
			FileInputStream fis = new FileInputStream(name);
			int content;
			this.fileSize = fis.available();
			while ((content = fis.read(packetDataSize)) != -1) {
				this.fileData.add(packetDataSize.clone());
				//System.out.println(new String(packetDataSize));
				//System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				int templastPacketSize = (packetDataSize.length < fis.available()) ? 499 :fis.available();
				lastPacketSize = (templastPacketSize == 0) ? lastPacketSize : templastPacketSize;
			}
			//System.out.println("Last packet was: " + lastPacketSize);
			if (this.lastPacketSize != 499) {
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
		
	}
	
	/**
	 * Sets the acknowledgement number of this specific download/upload.
	 */
	public void setAckNumber(int ack) {
		
	}
}