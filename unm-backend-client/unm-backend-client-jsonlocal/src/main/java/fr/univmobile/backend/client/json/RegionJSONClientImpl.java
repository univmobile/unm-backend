package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;

public class RegionJSONClientImpl implements RegionJSONClient {

	@Inject
	public RegionJSONClientImpl( //
			@Named("RegionJSONClientImpl") //
			final RegionClient regionClient) {

		this.regionClient = checkNotNull(regionClient, "regionClient");
	}

	private final RegionClient regionClient;

	@Override
	public String getRegionsJSON() throws IOException {

		final Region[] regions = regionClient.getRegions();

		final JSONMap json = new JSONMap();

		final JSONList list = new JSONList();

		json.put("region", list);

		for (final Region region : regions) {

			list.add(new JSONMap() //
					.put("id", region.getId()) //
					.put("label", region.getLabel()) //
					.put("url", region.getUrl()));
		}

		final String s = json.toJSONString();

		return s;
	}
}

class JSONList implements JSONAware {

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

class JSONMap implements JSONAware {

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