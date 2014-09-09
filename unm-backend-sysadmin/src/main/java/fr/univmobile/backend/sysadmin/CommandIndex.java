package fr.univmobile.backend.sysadmin;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * command-line arguments for command-line command: "index"
 */
@Parameters(commandDescription = "Load XML Data into Database indices")
class CommandIndex {

	@Parameter(names = { "-data", "-datadir", "-dataDir" }, required = true, description = "The local directory where the XML Data is stored", converter = FileConverter.class, validateValueWith = DataDirValidator.class)
	private File dataDir;

	@Parameter(names = { "-dbtype", "-dbType" }, required = false, description = "The DB Connection type, e.g.: mysql")
	private String dbType = "mysql";

	@Parameter(names = { "-dburl", "-dbUrl", "-dbURL" }, required = true, description = "The DB Connection URL, e.g.: jdbc:mysql://localhost:3306/univmobile")
	private String dbUrl;

	@Parameter(names = { "-dbusername", "-dbUsername", "-dbUserName" }, required = true, description = "The DB Connection username")
	private String dbUsername;

	@Parameter(names = { "-dbpassword", "-dbPassword" }, required = true, description = "The DB Connection password", password = true)
	private String dbPassword;

	public File getDataDir() {

		return dataDir;
	}

	public String getDbType() {

		return dbType;
	}

	public String getDbUrl() {
		
		return dbUrl;
	}
	
	public String getDbUsername() {
		
		return dbUsername;
	}
	
	public String getDbPassword() {

		return dbPassword;
	}
}
