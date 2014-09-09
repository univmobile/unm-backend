package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.univmobile.backend.core.impl.ConnectionType;

/**
 * command-line arguments for command-line command: "index"
 */
@Parameters(commandDescription = "Load XML Data into Database indices")
class CommandIndex extends AbstractDbCommand {

	@Parameter(names = { "-data", "-datadir", "-dataDir" }, required = true, description = "The local directory where the XML Data is stored", converter = FileConverter.class, validateValueWith = DataDirValidator.class)
	private File dataDir;

	public File getDataDir() {

		return dataDir;
	}

	@Override
	public void execute() throws IOException, SQLException,
			ParserConfigurationException, SAXException, ClassNotFoundException {

		System.out.println("Running: index...");

		final long start = System.currentTimeMillis();

		final File dataDir = getDataDir();

		final ConnectionType dbType = MYSQL;

		final Connection cxn = loadConnection();
		try {

			final AbstractTool indexation = new IndexationTool(dataDir, dbType,
					cxn);

			indexation.run();

		} finally {
			cxn.close();
		}

		final long elapsed = System.currentTimeMillis() - start;

		System.out.println("Elapsed time: " + elapsed + " ms.");

		System.out.println("Done.");
	}
}
