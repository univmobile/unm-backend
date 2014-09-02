package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import fr.univmobile.backend.core.UploadManager;
import fr.univmobile.backend.core.UploadNotFoundException;

public class UploadManagerImpl implements UploadManager {

	public UploadManagerImpl(final File uploadsDir) throws IOException {

		this.uploadsDir = checkNotNull(uploadsDir, "uploadsDir");

		if (!uploadsDir.isDirectory()) {
			throw new FileNotFoundException("uploadsDir is not a directory: "
					+ uploadsDir.getCanonicalPath());
		}
	}

	private final File uploadsDir;

	@Override
	public InputStream getUploadAsStream(final String uploadPath)
			throws IOException, UploadNotFoundException {

		final File uploadFile = getUploadFile(uploadPath);

		return new FileInputStream(uploadFile);
	}

	@Override
	public String getUploadMimeType(final String uploadPath)
			throws IOException, UploadNotFoundException {

		final File uploadFile = getUploadFile(uploadPath);

		final String filename = uploadFile.getName();

		if (filename.endsWith(".png")) {

			return "image/png";

		} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {

			return "image/jpeg";

		} else if (filename.endsWith(".gif")) {

			return "image/gif";

		} else {

			throw new IOException("Unknown mime type for filename: " + filename);
		}
	}

	/**
	 * @param uploadPath
	 *            e.g.
	 *            "/uploads/poi/d70eeb8c7837b9b1c2edd4a62511a7460b14c82b.jpg"
	 */
	private File getUploadFile(final String uploadPath) throws IOException,
			UploadNotFoundException {

		checkNotNull(uploadPath, "uploadPath");

		if (uploadPath.contains("./") || uploadPath.contains("../")) {
			throw new IllegalArgumentException("Illegal uploadPath: "
					+ uploadPath);
		}

		if (!uploadPath.startsWith("/uploads/")) {
			throw new IllegalArgumentException(
					"uploadPath should start with \"/uploads/\": " + uploadPath);
		}

		final String path = substringAfter(uploadPath, "/uploads/");

		final File uploadFile = new File(uploadsDir, path);

		if (!uploadFile.isFile()) {
			throw new UploadNotFoundException(uploadPath,
					new FileNotFoundException(uploadFile.getCanonicalPath()));
		}

		return uploadFile;
	}
}
