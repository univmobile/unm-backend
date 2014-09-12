package fr.univmobile.backend.search;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

import org.apache.commons.lang3.NotImplementedException;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.commons.datasource.Entry;

class Matcher {

	public Matcher(final SearchQuery query) {

		checkNotNull(query, "query");

		if (!SearchQueryText.class.isInstance(query)) {
			throw new NotImplementedException("query.class: "
					+ query.getClass());
		}

		items = ((SearchQueryText) query).getTextItems();

		locale = Locale.FRENCH;

		for (int i = 0; i < items.length; ++i) {

			items[i] = normalize(items[i]);
		}
	}

	private String normalize(final String text) {

		return text.toLowerCase(locale);
	}

	private final String[] items;

	public boolean match(final Entry<?> entry) {

		if (Comment.class.isInstance(entry)) {

			return match((Comment) entry);
		}

		throw new NotImplementedException("Unknown entry class: "
				+ entry.getClass().getName());
	}

	private boolean match(final Comment comment) {

		return match(comment.getPostedBy(), //
				Integer.toString(comment.getUid()), //
				comment.getMessage());
	}

	private final Locale locale;

	private boolean match(final String... strings) {

		final String[][] tokens = new String[strings.length][];

		for (int i = 0; i < strings.length; ++i) {

			tokens[i] = normalize(strings[i]) // .split("\\b");
					.split("[^\\p{L}0-9_]+");
		}

		items: for (final String item : items) {

			boolean found = false;

			for (final String[] words : tokens) {

				for (final String word : words) {

					if (item.equals(word)) {

						found = true;

						continue items;
					}
				}
			}

			if (!found) {
				return false;
			}
		}

		return true;
	}
}
