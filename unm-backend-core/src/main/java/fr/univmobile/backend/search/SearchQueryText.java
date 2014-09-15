package fr.univmobile.backend.search;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.split;

public class SearchQueryText extends SearchQuery {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -185067292850709739L;

	public SearchQueryText(final String text) {

		this.text = checkNotNull(text, "text");
	}

	private final String text;

	public String[] getTextItems() {

		return split(text);
	}
	
	@Override
	public String toString() {
		return text;
	}
}
