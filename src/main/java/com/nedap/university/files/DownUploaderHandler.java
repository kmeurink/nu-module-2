package com.nedap.university.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class takes care of all the separate downloads and uploads.
 * Also has the inputhandler and sender/receiver classes to communicate with the other side.
 * @author kester.meurink
 *
 */
public class DownUploaderHandler { //TODO make multithreaded? Depends on how the main function works
	//Named constants:
	private HashMap<Short, DownUploader> downUploaders;
	
	//Constructors:
	public DownUploaderHandler() {
		downUploaders = new HashMap<Short, DownUploader>();
	}
	
	
	//Queries:
		
	/**
	 * Returns the list of all active downloads/uploads.
	 * @return
	 */
	public HashMap<Short, DownUploader> getDownUploads() {
		return this.downUploaders;
	}
	
	/**
	 * Returns a specific download/upload.
	 * @return
	 */
	private DownUploader getDownUpload(short file) {
		return this.downUploaders.get(file);
	}
	
	/**
	 * Determines if a file is already being downloaded or uploaded.
	 * @param file
	 * @return
	 */
	public boolean checkFilePresence(boolean download, short file) {
		boolean filePresent = false;
		if (this.downUploaders.containsKey(file) && this.downUploaders.get(file).getStatus() == download) {
			filePresent = true;
		}
		return !filePresent;
	}
	
	//Commands:
	
	/**
	 * Adds new download/upload to the map
	 * @param download
	 */
	public void addDownUpload(Short fileNum, DownUploader load) {
		this.downUploaders.put(fileNum, load);
	}
	
	
	/**
	 * Removes download from the map
	 * @param download
	 */
	public void removeDownUpload(Short fileNum) {
		this.downUploaders.remove(fileNum);
	}
	
	/**
	 * Creates a new download DownUploader and adds it to the list.
	 * @param fileName
	 * @param fileNumber
	 */
	public void createDownload(String fileName, short fileNumber, boolean client) {
		DownUploader newDownload = new DownUploader();
		newDownload.setFileNumber(fileNumber);
		newDownload.setName(fileName, client);
		newDownload.setStatus(true);
		this.addDownUpload(fileNumber, newDownload);
	}
}
