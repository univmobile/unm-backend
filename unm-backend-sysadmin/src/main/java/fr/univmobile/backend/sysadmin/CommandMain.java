package fr.univmobile.backend.sysadmin;

import com.beust.jcommander.Parameter;

/**
 * main command-line arguments.
 */
class CommandMain {

	@Parameter(names = { "-h", "--help" }, help = true, arity = 0)
	private boolean help;
	
	public boolean isHelp() {
		
		return help;
	}
}
