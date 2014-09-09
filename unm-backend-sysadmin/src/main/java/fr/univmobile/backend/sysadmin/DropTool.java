package fr.univmobile.backend.sysadmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Code for the "drop" command-line tool.
 */
class DropTool extends AbstractTool {

	public DropTool(final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		super(dbType, cxn);
	}

	@Override
	public Result run() throws IOException, SQLException, SAXException {

		final int result = executeUpdate("dropAllTables");

		System.out.println("Done.");

		return new Result() {
			{
				setRowCount(result);
			}
		};
	}
}
