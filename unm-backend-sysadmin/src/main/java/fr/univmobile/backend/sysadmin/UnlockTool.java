package fr.univmobile.backend.sysadmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Code for the "unlock" command-line tool.
 */
class UnlockTool extends AbstractTool {

	public UnlockTool(final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		super(dbType, cxn);
	}

	@Override
	public void run() throws IOException, SQLException, SAXException {

		final int result = executeUpdate("unlockCategories");

		System.out.println("Done unlocking categories: " + result);
	}
}