package fr.univmobile.backend.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;

import org.junit.Test;

/**
 * Test the consistency among the 7400+ XML POI files in src/test/data/pois/002.
 * <p>
 * We cannot use the JUnit’s <code>RunWith(Parameterized.class)</code>
 * annotation because of issues with Java heap space when JUnit attempts to
 * populate a huge StringBuilder.
 */
public class Pois002ConsistencyTest extends AbstractPoisTest {

	public Pois002ConsistencyTest() {

		super(new File("src/test/data/pois/002"));
	}

	@Test
	public void testConsistency() throws Exception {

		boolean failure = false;

		int count = 0;

		for (final Map.Entry<Integer, Poi> entry : pois.getAllByInt("uid")
				.entrySet()) {

			++count;

			if (count % 1000 == 0) {

				System.out.println(count + "...");
			}

			final int uid = entry.getKey();
			final Poi poi = entry.getValue();

			final PoiTester tester = new PoiTester(uid, poi);

			try {

				tester.assertChildPoisHaveThisParent();

				tester.assertParentPoiHasThisChild();

			} catch (final AssertionError e) {

				System.out.println(tester + ": " + e);

				failure = true;
			}
		}

		System.out.println(count + " Done.");

		assertFalse(failure);
	}

	private class PoiTester {

		@Override
		public String toString() {

			return "poi" + uid + "_1.xml";
		}

		public PoiTester(final int uid, final Poi poi) {

			this.poi = checkNotNull(poi, "poi");
			this.uid = uid;
		}

		private final int uid;
		private final Poi poi;

		@Test
		public void assertParentPoiHasThisChild() throws Exception {

			if (poi.isNullParentUid()) {
				return;
			}

			if (!poi.isActive() || poi.isDeleted()) {
				return;
			}

			final int parentUid = poi.getParentUid();

			final Poi parentPoi = pois.getByUid(parentUid);

			for (final int childUid : parentPoi.getChildren()) {

				if (childUid == uid) {
					return;
				}
			}

			fail("Parent: " + parentUid + " doesn't have child: " + uid);
		}

		@Test
		public void assertChildPoisHaveThisParent() throws Exception {

			if (poi.isNullChildren()) {
				return;
			}

			for (final int childUid : poi.getChildren()) {

				final Poi childPoi = pois.getByUid(childUid);

				assertEquals(uid, childPoi.getParentUid());
			}
		}
	}
}
