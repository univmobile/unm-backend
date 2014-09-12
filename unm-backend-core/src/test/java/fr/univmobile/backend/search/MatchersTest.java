package fr.univmobile.backend.search;

import static fr.univmobile.backend.search.Matchers.normalize;
import static fr.univmobile.backend.search.Matchers.tokenize;
import static java.util.Locale.FRENCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class MatchersTest {

	@Test
	public void testTokenize_TrèsBien() {

		assertSet("Très", "bien").eq(tokenize("Très bien"));
	}

	@Test
	public void testTokenize_TrèsBien_normalize() {

		assertSet("très", "bien").eq(tokenize(normalize(FRENCH, "Très bien")));
	}

	@Test
	public void testTokenize_Numbers() {

		assertSet("Sami120", "va", "bien").eq(tokenize("Sami120 va bien"));
	}

	@Test
	public void testTokenize_Dash() {

		assertSet("haut-parleur", "haut", "parleur").eq(
				tokenize("haut-parleur"));
	}

	@Test
	public void testTokenize_CRLF() {

		assertSet("J", "aime", "bien", "l", "application", "mais", "si", "on",
				"danse").eq(
				tokenize("\nJ’aime bien l’application, mais si on danse ?\n"));
	}

	@Test
	public void testTokenize_minus1() {

		assertSet("-1", "1").eq(tokenize(" -1"));
	}

	private static SetEquals assertSet(final String... s) {

		return new SetEquals(s);
	}

	private static class SetEquals {

		public SetEquals(final String[] ref) {

			for (final String s : ref) {

				this.ref.add(s);
			}
		}

		private final Set<String> ref = new HashSet<String>();

		public void eq(final String[] test) {

			final Set<String> t = new HashSet<String>();

			for (final String s : test) {

				t.add(s);
			}

			for (final String s : t) {

				assertTrue("Unexpected token: [" + s + "]", ref.contains(s));
			}

			for (final String s : ref) {

				assertTrue("Expected: " + s + ", but was:", t.contains(s));
			}

			assertEquals("set.length", ref.size(), t.size()); // Unnecessary
		}
	}
}
