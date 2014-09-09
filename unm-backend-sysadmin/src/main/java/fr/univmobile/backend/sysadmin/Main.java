package fr.univmobile.backend.sysadmin;

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
		final CommandLock commandLock = new CommandLock();
		final CommandUnlock commandUnlock = new CommandUnlock();
		final CommandDrop commandDrop = new CommandDrop();

		jc.addCommand("index", commandIndex);
		jc.addCommand("lock", commandLock);
		jc.addCommand("unlock", commandUnlock);
		jc.addCommand("drop", commandDrop);

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

		final AbstractDbCommand dbCommand;

		if ("index".equals(command)) {

			dbCommand = commandIndex;

		} else if ("lock".equals(command)) {

			dbCommand = commandLock;

		} else if ("unlock".equals(command)) {

			dbCommand = commandUnlock;

		} else if ("drop".equals(command)) {

			dbCommand = commandDrop;

		} else {

			System.err.println("Illegal command: " + command);

			jc.usage();

			return;
		}

		dbCommand.execute();
	}
}
