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
//TODO perhaps combine uploader and downloader, as they should have the same methods.
public class Downloader {//TODO work out how it should work.
	
	//Named constants:
	private String fileName;
	private int fileSize;
	private List<byte[]> fileData; //TODO is list the best container?
	private String filePath= ""; //TODO determine how to choose this.
	private File file;
	private byte[] packetDataSize = new byte[499]; //TODO be able to set this size.
	
	//Constructors:
	public Downloader() {
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
	 * Determines the size of the total file.
	 * @return
	 */
	private int determineSize() {
		int size = 0;
		for (byte[] i : this.fileData) {
			size += i.length;
		}
		return size;
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
			fos.write(finalFile);
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
		try {
			//File readFile = new File(name);
			FileInputStream fis = new FileInputStream(name);
			this.fileSize = fis.available();
			int content;
			while ((content = fis.read(packetDataSize)) != -1) {
				this.fileData.add(packetDataSize);
			}
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
	
	public byte[] sendDataSingle(int dataBlock) { //TODO which is better for the filehandler
		return this.fileData.get(dataBlock);
	}
	
	
}
