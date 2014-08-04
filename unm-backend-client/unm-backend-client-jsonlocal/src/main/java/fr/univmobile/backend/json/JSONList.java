package fr.univmobile.backend.json;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

public class JSONList implements JSONAware {

	private final JSONArray jsonArray = new JSONArray();

	@SuppressWarnings("unchecked")
	private final List<Object> list = (List<Object>) jsonArray;

	public JSONList add(final Object item) {

		list.add(item);

		return this;
	}

	@Override
	public String toJSONString() {

		return jsonArray.toJSONString();
	}
}
