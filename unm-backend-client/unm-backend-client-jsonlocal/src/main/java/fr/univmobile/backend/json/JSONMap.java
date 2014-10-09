package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Nullable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class JSONMap implements JSONAware {

	@Override
	public String toJSONString() {

		return jsonObject.toJSONString();
	}

	private final JSONObject jsonObject = new JSONObject();

	@SuppressWarnings("unchecked")
	private final Map<String, Object> map = (Map<String, Object>) jsonObject;

	private JSONMap putAny(final String key, @Nullable final Object value) {

		checkNotNull("key", key);

		map.put(key, value); // Even when value == null

		return this;
	}

	public JSONMap put(final String key, @Nullable final JSONAware value) {

		return putAny(key, value);
	}

	public JSONMap put(final JSONObject json) {

		checkNotNull(json, "json");

		for (final Object key : json.keySet()) {

			final JSONObject value = (JSONObject) json.get(key);

			put(key.toString(), value);
		}

		return this;
	}

	public JSONMap put(final String key, @Nullable final String value) {

		return putAny(key, value);
	}

	public JSONMap put(final String key, final int value) {

		return putAny(key, value);
	}

	public JSONMap put(final String key, @Nullable final double value) {

		return putAny(key, Double.toString(value));
	}
}