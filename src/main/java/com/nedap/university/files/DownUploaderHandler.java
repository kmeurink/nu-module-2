package com.nedap.university.files;

import java.util.ArrayList;
import java.util.List;

/**
 * Class takes care of all the separate downloads and uploads.
 * Also has the inputhandler and sender/receiver classes to communicate with the other side.
 * @author kester.meurink
 *
 */
public class DownUploaderHandler { //TODO make multithreaded? Depends on how the main function works
	//Named constants:
	private List<DownUploader> downUploaders;
	
	//Constructors:
	public DownUploaderHandler() {
		downUploaders = new ArrayList<DownUploader>();
	}
	
	
	//Queries:
		
	/**
	 * Returns the list of all active downloads/uploads.
	 * @return
	 */
	public List<DownUploader> getDownUploads() {
		return this.downUploaders;
	}
	
	/**
	 * Returns a specific download/upload.
	 * @return
	 */
	private DownUploader getDownUpload(int index) {
		return this.downUploaders.get(index);
	}
	
	//Commands:
	
	/**
	 * Adds new download/upload to the list
	 * @param download
	 */
	public void addDownUpload(DownUploader load) {
		this.downUploaders.add(load);
	}
	
	
	/**
	 * Removes download from the list
	 * @param download
	 */
	public void removeDownUpload(DownUploader load) {
		this.downUploaders.remove(load);
	}
		
}
