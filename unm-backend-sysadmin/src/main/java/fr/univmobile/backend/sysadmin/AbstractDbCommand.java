package fr.univmobile.backend.sysadmin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.beust.jcommander.Parameter;

/**
 * common command-line arguments for unm-backend-sysadmin tools.
 */
abstract class AbstractDbCommand {

	@Parameter(names = { "-dbtype", "-dbType" }, required = false, description = "The DB Connection type, e.g.: mysql")
	private String dbType = "mysql";

	@Parameter(names = { "-dburl", "-dbUrl", "-dbURL" }, required = true, description = "The DB Connection URL, e.g.: jdbc:mysql://localhost:3306/univmobile")
	private String dbUrl;

	@Parameter(names = { "-dbusername", "-dbUsername", "-dbUserName" }, required = true, description = "The DB Connection username")
	private String dbUsername;

	@Parameter(names = { "-dbpassword", "-dbPassword" }, required = true, description = "The DB Connection password", password = true)
	private String dbPassword;

	public final String getDbType() {

		return dbType;
	}

	public final String getDbUrl() {

		return dbUrl;
	}

	public final String getDbUsername() {

		return dbUsername;
	}

	public final String getDbPassword() {

		return dbPassword;
	}

	protected final Connection loadConnection() throws SQLException,
			ClassNotFoundException {

		// final ConnectionType dbType;

		if ("mysql".equals(getDbType())) {
			// dbType = MYSQL;
			Class.forName("com.mysql.jdbc.Driver");
		} else {
			throw new IllegalArgumentException("Unknown dbType: " + getDbType()
					+ ", should be: \"mysql\" or \"h2\"");
		}

		final String url = getDbUrl();
		final String username = getDbUsername();
		final String password = getDbPassword();

		final Connection cxn = DriverManager.getConnection(url, username,
				password);

		return cxn;
	}

	public abstract void execute() throws Exception;
}
