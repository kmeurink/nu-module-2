package com.nedap.university.files;

import java.io.File;
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
	private File directory;
	
	//Constructors:
	public DownUploaderHandler(File directory) {
		downUploaders = new HashMap<Short, DownUploader>();
		this.directory = directory;
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
	
	public String[] getActiveFiles() {
		String[] activeFiles = new String[this.downUploaders.size()];
		String separatorIntra = ":";
		int count = 0;
		for (DownUploader i : this.downUploaders.values()) {
			String fileInformation = "";
			fileInformation += i.getFileNumber();
			fileInformation += separatorIntra;
			fileInformation += i.getName();
			activeFiles[count] = fileInformation;
			count++;
		}
		return activeFiles;
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
	 * Creates a new download or upload DownUploader and adds it to the list.
	 * @param fileName
	 * @param fileNumber
	 */
	public void createFileload(String fileName, short fileNumber, boolean client, boolean down) {
		DownUploader newDownload = new DownUploader();
		newDownload.setDirectory(directory);
		newDownload.setFileNumber(fileNumber);
		newDownload.setName(fileName, client);
		newDownload.setStatus(down);
		this.addDownUpload(fileNumber, newDownload);
	}
}
