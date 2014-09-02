package fr.univmobile.backend.core.impl;

import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.junit.Test;

import fr.univmobile.backend.core.AbstractPoisTest;
import fr.univmobile.backend.core.Poi;

public class PoiTreeUtils003Test extends AbstractPoisTest {

	public PoiTreeUtils003Test() {

		super(new File("src/test/data/pois/003"));
	}

	@Test
	public void testCount() throws Exception {

		assertEquals(277, pois.getAllByInt("uid").size());
	}

	@Test
	public void testPoiTreeUtils() throws Exception {

		final PoiTreeUtils tree = new PoiTreeUtils(pois);

		final OutputStream os = new FileOutputStream(new File("target",
				"testPoiTree003.xml"));
		try {

			final PrintWriter pw = new PrintWriter(new OutputStreamWriter(os,
					UTF_8));

			tree.dump(pw);

			pw.flush();

		} finally {
			os.close();
		}

		final Poi[] roots = tree.getRoots();
		
		assertEquals(5, roots.length);
	}
}
