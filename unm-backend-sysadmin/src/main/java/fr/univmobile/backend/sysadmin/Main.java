package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.sysadmin.ConnectionType.MYSQL;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * entry point for the unm-backend-sysadmin.jar utility.
 */
public class Main {

	public static void main(final String... args) throws Exception {

		final CommandMain commandMain = new CommandMain();

		final JCommander jc = new JCommander(commandMain);

		jc.setProgramName("java -jar unm-backend-sysadmin.jar");

		final CommandIndex commandIndex = new CommandIndex();

		jc.addCommand("index", commandIndex);

		try {

			jc.parse(args);

		} catch (final ParameterException e) {

			System.err.println(e);

			jc.usage();

			return;
		}

		if (commandMain.isHelp()) {

			jc.usage();

			return;
		}

		final String command = jc.getParsedCommand();

		if ("index".equals(command)) {

			execute(commandIndex);

			return;
		}

		System.err.println("Illegal command: " + command);

		jc.usage();

		return;
	}

	private static void execute(final CommandIndex commandIndex)
			throws IOException, SQLException, ParserConfigurationException,
			SAXException, ClassNotFoundException {

		System.out.println("Running: index...");

		final long start = System.currentTimeMillis();

		final ConnectionType dbType;

		if ("mysql".equals(commandIndex.getDbType())) {
			dbType = MYSQL;
			Class.forName("com.mysql.jdbc.Driver");
		} else {
			throw new IllegalArgumentException("Unknown dbType: "
					+ commandIndex.getDbType()
					+ ", should be: \"mysql\" or \"h2\"");
		}

		final String url = commandIndex.getDbUrl();
		final String username = commandIndex.getDbUsername();
		final String password = commandIndex.getDbPassword();

		final File dataDir = commandIndex.getDataDir();

		final Connection cxn = DriverManager.getConnection(url, username,
				password);
		try {

			final Tool indexation = new Indexation(dataDir, dbType, cxn);

			indexation.run();

		} finally {
			cxn.close();
		}

		final long elapsed = System.currentTimeMillis() - start;

		System.out.println("Elapsed time: " + elapsed + " ms.");

		System.out.println("Done.");
	}
}
