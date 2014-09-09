package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.sysadmin.ConnectionType.MYSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.beust.jcommander.Parameters;

/**
 * command-line arguments for command-line command: "lock"
 */
@Parameters(commandDescription = "Manage index locks")
class CommandLock extends AbstractDbCommand {

	@Override
	public void execute() throws IOException, SQLException,
			ParserConfigurationException, SAXException, ClassNotFoundException {

		final ConnectionType dbType = MYSQL;

		final Connection cxn = loadConnection();
		try {

			final AbstractTool lock = new LockTool(dbType, cxn);

			lock.run();

		} finally {
			cxn.close();
		}
	}
}
