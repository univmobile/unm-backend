package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.beust.jcommander.Parameters;

import fr.univmobile.backend.core.impl.ConnectionType;

/**
 * command-line arguments for command-line command: "unlock"
 */
@Parameters(commandDescription = "Manage index locks")
class CommandUnlock extends AbstractDbCommand {

	@Override
	public void execute() throws IOException, SQLException,
			ParserConfigurationException, SAXException, ClassNotFoundException {

		final ConnectionType dbType = MYSQL;

		final Connection cxn = loadConnection();
		try {

			final AbstractTool unlock = new UnlockTool(dbType, cxn);

			unlock.run();

		} finally {
			cxn.close();
		}
	}
}
