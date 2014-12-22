package fr.univmobile.backend.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public abstract class AbstractImageMapsTest {

	protected AbstractImageMapsTest(final File originalDataDir) {

		this.originalDataDir = originalDataDir;
	}

	protected final File originalDataDir;

	/**
	 * Prepare le test afin de ne loader qu'une seule fois pour tous les tests les donnees
	 * dans les fichiers XML
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		imageMaps = imageMapsByClass.get(this.getClass());

		if (imageMaps != null) { // Load data only once
			return;
		}

		final long start = System.currentTimeMillis();

		System.out.println("Copying data...");

		final File tmpDataDir = new File("target/"
				+ this.getClass().getSimpleName());

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		final long elapsedCopy = System.currentTimeMillis() - start;

		System.out.println("Copying data: Done. (" + elapsedCopy + " ms.)");

		System.out.println("Loading data...");

		imageMaps = BackendDataSourceFileSystem.newDataSource(ImageMapDataSource.class,
				tmpDataDir);

		final long elapsedLoad = System.currentTimeMillis() - start
				- elapsedCopy;

		System.out.println("Loadind data: Done. (" + elapsedLoad + " ms.)");

		imageMapsByClass.put(this.getClass(), imageMaps);
	}

	protected ImageMapDataSource imageMaps;

	private static final Map<Class<? extends AbstractImageMapsTest>, ImageMapDataSource> imageMapsByClass = new HashMap<Class<? extends AbstractImageMapsTest>, ImageMapDataSource>();

}
