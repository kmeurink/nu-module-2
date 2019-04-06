package com.nedap.university.files;

import java.util.ArrayList;
import java.util.List;

public class FileHandler {
	//Named constants:
	private List<Downloader> downloaders;
	private List<Uploader> uploaders;
	
	//Constructors:
	public FileHandler() {
		downloaders = new ArrayList<Downloader>();
		uploaders = new ArrayList<Uploader>();

	}
	
	
	//Queries:
	
	/**
	 * Returns the list of all downloads.
	 * @return
	 */
	public List<Downloader> getDownloads() {
		return this.downloaders;
	}
	
	/**
	 * Returns the list of all uploads.
	 * @return
	 */
	public List<Uploader> getUploads() {
		return this.uploaders;
	}
	
	
	//Commands:
	
	/**
	 * Adds new download to the list
	 * @param download
	 */
	public void addDownload(Downloader download) {
		this.downloaders.add(download);
	}
	
	/**
	 * Adds new upload to the list
	 * @param upload
	 */
	public void addUpload(Uploader upload) {
		this.uploaders.add(upload);
	}
	
	/**
	 * Removes download from the list
	 * @param download
	 */
	public void removeDownload(Downloader download) {
		this.downloaders.remove(download);
	}
	
	/**
	 * Removes upload from the list
	 * @param upload
	 */
	public void removeUpload(Uploader upload) {
		this.uploaders.remove(upload);
	}
	
}
