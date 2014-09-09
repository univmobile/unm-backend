package fr.univmobile.backend.sysadmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.univmobile.backend.core.impl.ConnectionType;

/**
 * Code for the "unlock" command-line tool.
 */
class UnlockTool extends AbstractTool {

	public UnlockTool(final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		super(dbType, cxn);
	}

	@Override
	public Result run() throws IOException, SQLException, SAXException {

		final int result = executeUpdate("unlockCategories");

		System.out.println("Done unlocking categories: " + result);

		return new Result(result);
	}
}
