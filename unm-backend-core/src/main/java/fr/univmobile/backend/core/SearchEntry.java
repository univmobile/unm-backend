package fr.univmobile.backend.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

public class SearchEntry {

	public SearchEntry(final String category, final int entityId) {

		this.category = checkNotNull(category, "category");
		this.entityId = entityId;
	}

	public final String category;

	public final int entityId;

	public void addField(final String name, @Nullable final String value) {

		checkNotNull(name, "name");

		if (isBlank(value)) {
			return;
		}

		fields.put(name, value);
	}

	private final Map<String, String> fields = new HashMap<String, String>();

	public Iterable<Entry<String, String>> fields() {

		return fields.entrySet();
	}
}
