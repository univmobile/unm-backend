package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

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
	public RegionJSONClientImpl(@Named("RegionJSONClientImpl")//
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

		json.put("regions", list);

		for (final Region region : regions) {

			final String url = region.getUrl();

			list.add(new JSONMap() //
					.put("id", region.getId()) //
					.put("label", region.getLabel()) //
					.put("url", url) //
					.put("pois", new JSONMap() //
							.put("count", region.getPoiCount()) //
							.put("url", region.getPoisUrl())));
		}

		return json.toJSONString();
	}

	@Override
	public String getUniversitiesJSONByRegion(final String regionId)
			throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getUniversitiesJSONByRegion():" + regionId + "...");
		}

		final University[] universities = regionClient
				.getUniversitiesByRegion(regionId);

		final JSONMap json = new JSONMap().put("id", regionId).put("label",
				getRegionLabelById(regionId));

		final JSONList list = new JSONList();

		json.put("universities", list);

		for (final University university : universities) {

			list.add(new JSONMap() //
					.put("id", university.getId()) //
					.put("title", university.getTitle()) //
					.put("config", new JSONMap() //
							.put("url", university.getConfigUrl())) //
					.put("pois", new JSONMap() //
							.put("count", university.getPoiCount()) //
							.put("url", university.getPoisUrl()))
					//
					.put("shibboleth",
							new JSONMap() //
									.put("identityProvider", university
											.getShibbolethIdentityProvider())) //
			);
		}

		final String s = json.toJSONString();

		if (log.isDebugEnabled()) {
			log.debug("getUniversitiesJSONByRegion():" + regionId + ":" + s);
		}

		return s;
	}

	private String getRegionLabelById(final String regionId) throws IOException {

		checkNotNull(regionId, "regionId");

		for (final Region region : regionClient.getRegions()) {

			if (regionId.equals(region.getId())) {

				return region.getLabel();
			}
		}

		throw new IllegalArgumentException("Unknown regionId: " + regionId);
	}
}
