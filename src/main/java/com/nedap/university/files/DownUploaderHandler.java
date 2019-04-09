package com.nedap.university.files;

import java.util.ArrayList;
import java.util.List;

/**
 * Class takes care of all the separate downloads and uploads.
 * Also has the inputhandler and sender/receiver classes to communicate with the other side.
 * @author kester.meurink
 *
 */
public class FileHandler { //TODO make multithreaded? Depends on how the main function works
	//Named constants:
	private List<DownUploader> downloaders;
	private List<DownUploader> uploaders;
	
	//Constructors:
	public FileHandler() {
		downloaders = new ArrayList<DownUploader>();
		uploaders = new ArrayList<DownUploader>();

	}
	
	
	//Queries:
	
	/**
	 * Returns the list of all downloads.
	 * @return
	 */
	public List<DownUploader> getDownloads() {
		return this.downloaders;
	}
	
	/**
	 * Returns the list of all uploads.
	 * @return
	 */
	public List<DownUploader> getUploads() {
		return this.uploaders;
	}
	
	
	//Commands:
	
	/**
	 * Adds new download to the list
	 * @param download
	 */
	public void addDownload(DownUploader download) {
		this.downloaders.add(download);
	}
	
	/**
	 * Adds new upload to the list
	 * @param upload
	 */
	public void addUpload(DownUploader upload) {
		this.uploaders.add(upload);
	}
	
	/**
	 * Removes download from the list
	 * @param download
	 */
	public void removeDownload(DownUploader download) {
		this.downloaders.remove(download);
	}
	
	/**
	 * Removes upload from the list
	 * @param upload
	 */
	public void removeUpload(DownUploader upload) {
		this.uploaders.remove(upload);
	}
	
}
