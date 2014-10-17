package fr.univmobile.backend.sysadmin.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.univmobile.backend.sanitycheck.BackendChecker;

@RunWith(Parameterized.class)
public class DataSanityCheck {

	@Parameters(name = "{0}")
	public static Collection<Object[]> parameters() throws IOException {

		final Collection<Object[]> parameters = new ArrayList<Object[]>();

		for (final File dir : new File("src/test/data").listFiles()) {

			if (!dir.isDirectory()) {
				continue;
			}

			parameters.add(new Object[] { "dir:" + dir.getName(), dir });
		}

		return parameters;
	}

	public DataSanityCheck(final String dirLabel, final File dataDir) {

		this.dataDir = dataDir;
	}

	@Before
	public void setUp() throws Exception {

		checker = new BackendChecker();
	}

	private BackendChecker checker;

	private final File dataDir;

	@Test
	public void testData() throws Exception {

		checker.checkDirectories(dataDir);
	}
}
