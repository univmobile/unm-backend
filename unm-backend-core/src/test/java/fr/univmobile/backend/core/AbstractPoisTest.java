package fr.univmobile.backend.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public abstract class AbstractPoisTest {

	protected AbstractPoisTest(final File originalDataDir) {

		this.originalDataDir = originalDataDir;
	}

	private final File originalDataDir;

	@Before
	public final void setUp() throws Exception {

		pois = poisByClass.get(this.getClass());

		if (pois != null) { // Load data only once
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

		pois = BackendDataSourceFileSystem.newDataSource(PoiDataSource.class,
				tmpDataDir);

		final long elapsedLoad = System.currentTimeMillis() - start
				- elapsedCopy;

		System.out.println("Loadind data: Done. (" + elapsedLoad + " ms.)");

		poisByClass.put(this.getClass(), pois);
	}

	protected PoiDataSource pois;

	private static final Map<Class<? extends AbstractPoisTest>, PoiDataSource> poisByClass = new HashMap<Class<? extends AbstractPoisTest>, PoiDataSource>();
}
