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

	public JSONMap put(final String key, @Nullable final Object value) {

		checkNotNull("key", key);

		map.put(key, value); // Even when value == null

		return this;
	}
}