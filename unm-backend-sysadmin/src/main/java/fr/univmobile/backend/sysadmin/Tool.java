package fr.univmobile.backend.sysadmin;

import java.io.IOException;
import java.sql.SQLException;

import org.xml.sax.SAXException;

/**
 * Common code for the command-line tools.
 */
abstract class Tool {

	protected Tool() {

	}

	public abstract void run() throws IOException, SQLException, SAXException;
}
