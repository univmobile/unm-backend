package fr.univmobile.backend.core;

import java.io.IOException;
import java.io.InputStream;

public interface UploadManager {

	/**
	 * @param uploadPath e.g. "/uploads/poi/d70eeb8c7837b9b1c2edd4a62511a7460b14c82b.jpg"
	 */
	InputStream getUploadAsStream(String uploadPath) throws IOException,
			UploadNotFoundException;

	/**
	 * @param uploadPath e.g. "/uploads/poi/d70eeb8c7837b9b1c2edd4a62511a7460b14c82b.jpg"
	 */
	String getUploadMimeType(String uploadPath) throws IOException,
			UploadNotFoundException;
}
