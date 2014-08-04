package fr.univmobile.backend.json;

import java.util.Map;

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

	public JSONMap put(final String key, final Object value) {

		map.put(key, value);

		return this;
	}
}