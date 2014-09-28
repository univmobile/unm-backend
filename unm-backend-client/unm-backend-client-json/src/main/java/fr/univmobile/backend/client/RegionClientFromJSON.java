package fr.univmobile.backend.client;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.RegionJSONClient;

public class RegionClientFromJSON extends
		AbstractClientFromJSON<RegionJSONClient> implements RegionClient {

	@Inject
	public RegionClientFromJSON(final RegionJSONClient jsonClient) {

		super(jsonClient);
	}

	private static Log log = LogFactory.getLog(RegionClientFromJSON.class);

	@Override
	public Region[] getRegions() throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getRegions()...");
		}

		return unmarshall(jsonClient.getRegionsJSON(), RegionsJSON.class)
				.getRegions();
	}

	@Override
	public University[] getUniversitiesByRegion(final String regionId)
			throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getUniversitiesByRegion():" + regionId + "...");
		}

		return unmarshall(jsonClient.getUniversitiesJSONByRegion(regionId),
				UniversitiesJSON.class).getUniversities();
	}

	@XPath("/*")
	public interface RegionsJSON {

		@XPath("regions")
		RegionJSON[] getRegions();

		interface RegionJSON extends Region {

			@XPath("@id")
			@Override
			String getId();

			@XPath("@label")
			@Override
			String getLabel();

			@XPath("@url")
			@Override
			String getUrl();

			@XPath("pois/@count")
			@Override
			int getPoiCount();

			@XPath("pois/@url")
			@Override
			String getPoisUrl();
		}
	}

	@XPath("/*")
	public interface UniversitiesJSON {

		@XPath("universities")
		UniversityJSON[] getUniversities();

		interface UniversityJSON extends University {

			@XPath("@id")
			@Override
			String getId();

			@XPath("@title")
			@Override
			String getTitle();

			@XPath("config/@url")
			@Override
			String getConfigUrl();

			@XPath("pois/@count")
			@Override
			int getPoiCount();

			@XPath("pois/@url")
			@Override
			String getPoisUrl();
			
			@XPath("shibboleth/@identityProvider")
			@Override
			@Nullable
			String getShibbolethIdentityProvider();
		}
	}
}
