package fr.univmobile.backend.sysadmin.data;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.sanitycheck.BackendChecker;

public class DataSanityCheck {

	@Before
	public void setUp() throws Exception {
		
		checker = new BackendChecker();
	}
	
	private BackendChecker checker; 
			
	@Test
	public void testUsersData() throws Exception {
		
		for (final File dir:new File("src/test/data").listFiles()) {
			
			if (!dir.isDirectory()) {
				continue;
			}
			
			checker.checkDirectories(dir);
		}
	}
}
