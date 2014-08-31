package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/regions/${regionId}" })
public class UniversitiesJSONController extends AbstractJSONController {

	@PathVariable("${regionId}")
	private String getRegionId() {

		return getPathStringVariable("${regionId}");
	}

	public UniversitiesJSONController(final RegionDataSource regions,
			final RegionJSONClient regionJSONClient) {

		this.regions = checkNotNull(regions, "regions");
		this.regionJSONClient = checkNotNull(regionJSONClient,
				"regionJSONClient");
	}

	private final RegionDataSource regions;
	private final RegionJSONClient regionJSONClient;

	private static final Log log = LogFactory
			.getLog(UniversitiesJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException,
			TransactionException, PageNotFoundException {

		log.debug("actionJSON()...");

		String regionId = getRegionId();

		if (regionId.endsWith(".json")) {
			regionId = substringBeforeLast(regionId, ".json");
		}

		if (log.isInfoEnabled()) {
			log.info("regionId: " + regionId);
		}

		if (regions.isNullByUid(regionId)) {
			throw new PageNotFoundException();
		}

		final String universityJSON = regionJSONClient
				.getUniversitiesJSONByRegion(regionId);

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/regions/" + regionId) + "\","
				+ substringAfter(universityJSON, "{");

		return json;
	}
}
