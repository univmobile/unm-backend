package fr.univmobile.backend.search;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.google.common.collect.Iterables;

public abstract class Matchers {

	public static String[] tokenize(final String text) {

		checkNotNull(text, "text");

		final String trim = text.trim();

		final String[] s0 = trim.split("[^\\p{L}0-9_\\-]+");

		final String[] s1 = trim.split("[^\\p{L}0-9_]+"); // .split("\\b");

		final Set<String> set = new HashSet<String>();

		for (final String s : s0) {

			set.add(s);
		}

		for (final String s : s1) {

			set.add(s);
		}

		set.remove(""); // ...
		
		return Iterables.toArray(set, String.class);
	}

	public static String normalize(final Locale locale, final String text) {

		return text.toLowerCase(locale);
	}
}
