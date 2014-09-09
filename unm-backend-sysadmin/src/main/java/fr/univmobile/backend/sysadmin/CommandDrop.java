package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.sysadmin.ConnectionType.MYSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.beust.jcommander.Parameters;

/**
 * command-line arguments for command-line command: "drop"
 */
@Parameters(commandDescription = "Drop all indexation tables")
class CommandDrop extends AbstractDbCommand {

	@Override
	public void execute() throws IOException, SQLException,
			ParserConfigurationException, SAXException, ClassNotFoundException {

		final ConnectionType dbType = MYSQL;

		final Connection cxn = loadConnection();
		try {

			final AbstractTool unlock = new DropTool(dbType, cxn);

			unlock.run();

		} finally {
			cxn.close();
		}
	}
}
