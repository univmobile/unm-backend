package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.University;
import fr.univmobile.backend.json.JSONList;
import fr.univmobile.backend.json.JSONMap;

public class RegionJSONClientImpl implements RegionJSONClient {

	@Inject
	public RegionJSONClientImpl( //
			final String baseURL, @Named("RegionJSONClientImpl")//
			final RegionClient regionClient) {

		if (isBlank(baseURL)) {
			throw new IllegalArgumentException("Argument is mandatory: baseURL");
		}

		this.baseURL = checkNotNull(baseURL, "baseURL");
		this.regionClient = checkNotNull(regionClient, "regionClient");
	}

	private final String baseURL;
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
					.put("url", filterURL(region.getUrl())));
		}

		return json.toJSONString();
	}

	private String filterURL(final String url) {

		if (!url.contains("${baseURL}")) {
			return url;
		}

		String u = url;

		if (baseURL.endsWith("/")) {
			u = u.replace("${baseURL}/", baseURL);
		}

		u = u.replace("${baseURL}", baseURL);

		return u;
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
