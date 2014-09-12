package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nullable;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.univmobile.backend.core.impl.ConnectionType;

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
					getQuery(), dbType, cxn);

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

	@Parameter(names = { "-q", "-query", "-search" }, description = "Search among comments")
	private String query;

	public String getQuery() {

		return query;
	}

	@Parameter(names = { "-a", "-add" }, description = "Add a new comment", arity = 0)
	private boolean add;

	public boolean isAdd() {

		return add;
	}

	@Parameter(names = { "-u", "-user" }, description = "The User's UID to add a new comment with")
	private String user;

	@Nullable
	public String getUser() {

		return user;
	}

	@Parameter(names = { "-m", "-message" }, description = "The comment message")
	private String message;

	@Nullable
	public String getMessage() {

		return message;
	}
}
