package fr.univmobile.backend.sysadmin;

import java.io.File;

import com.beust.jcommander.IStringConverter;

/**
 * String-File converter for command-line parameter: "-data" (= dataDir)
 */
public class FileConverter implements IStringConverter<File> {

	@Override
	public File convert(final String path) {

		return new File(path);
	}
}
