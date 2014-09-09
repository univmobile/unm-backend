package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.sysadmin.ConnectionType.MYSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * command-line arguments for command-line command: "comment"
 */
@Parameters(commandDescription = "Manage comments")
class CommandComment extends AbstractDbCommand {

	@Override
	public void execute() throws IOException, SQLException,
			ParserConfigurationException, SAXException, ClassNotFoundException {

		final ConnectionType dbType = MYSQL;

		final Connection cxn = loadConnection();
		try {

			final AbstractTool commentTool = new CommentTool(getLimit(),
					dbType, cxn);

			commentTool.run();

		} finally {
			cxn.close();
		}
	}

	@Parameter(names = { "-l", "-limit" }, description = "The maximum number of records to return")
	private int limit = 30;

	public int getLimit() {

		return limit;
	}

}
