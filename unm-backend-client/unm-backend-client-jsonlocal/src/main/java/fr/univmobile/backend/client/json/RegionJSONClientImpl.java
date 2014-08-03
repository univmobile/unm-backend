package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.University;

public class RegionJSONClientImpl implements RegionJSONClient {

	@Inject
	public RegionJSONClientImpl( //
			@Named("RegionJSONClientImpl")//
			final RegionClient regionClient) {

		this.regionClient = checkNotNull(regionClient, "regionClient");
	}

	private final RegionClient regionClient;

	private static final Log log = LogFactory
			.getLog(RegionJSONClientImpl.class);

	@Override
	public String getRegionsJSON() throws IOException {

		log.debug("getRegionsJSON()...");

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

	@Override
	public String getUniversitiesJSONByRegion(final String regionId)
			throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getUniversitiesJSONByRegion():" + regionId + "...");
		}

		final University[] universities = regionClient
				.getUniversitiesByRegion(regionId);

		final JSONMap json = new JSONMap();

		final JSONList list = new JSONList();

		json.put("universities", list);

		for (final University university : universities) {

			list.add(new JSONMap() //
					.put("id", university.getId()) //
					.put("title", university.getTitle()));
		}

		final String s = json.toJSONString();

		if (log.isDebugEnabled()) {
			log.debug("getUniversitiesJSONByRegion():" + regionId + ":" + s);
		}

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