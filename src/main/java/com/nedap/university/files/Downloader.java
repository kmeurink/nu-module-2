package com.nedap.university.files;

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
	
	
	
	//Constructors:
	public Downloader() {
		
	}
	
	
	//Queries:
	
	/**
	 * Returns the name of the downloading file.
	 * @return
	 */
	public String getName() {
		return this.fileName;
	}
	
	/**
	 * Returns the size of the downloading file.
	 * @return
	 */
	public int getSizee() {
		return this.fileSize;
	}
	
	
	//Commands:
	
	
	
	
}
