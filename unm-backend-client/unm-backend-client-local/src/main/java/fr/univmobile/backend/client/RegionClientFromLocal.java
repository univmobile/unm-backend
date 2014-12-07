package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.DataBeans;

public class RegionClientFromLocal extends AbstractClientFromLocal implements
		RegionClient {

	@Inject
	public RegionClientFromLocal(final String baseURL,
			final RegionDataSource regions, final PoiDataSource pois) {

		super(baseURL);

		this.regions = checkNotNull(regions, "regions");
		this.pois = checkNotNull(pois, "pois");
	}

	private final PoiDataSource pois;
	private final RegionDataSource regions;

	private static final Log log = LogFactory
			.getLog(RegionClientFromLocal.class);

	@Override
	public Region[] getRegions() throws IOException {

		log.debug("getRegions()...");

		final Map<String, fr.univmobile.backend.core.Region> dsRegions //
		= regions.getAllBy(String.class, "uid");

		final Region[] regionsArray = new Region[dsRegions.size()];

		int i = 0;

		final Set<fr.univmobile.backend.core.Region> sortedSet = new TreeSet<fr.univmobile.backend.core.Region>(
				new Comparator<fr.univmobile.backend.core.Region>() {

					@Override
					public int compare(
							final fr.univmobile.backend.core.Region r1,
							final fr.univmobile.backend.core.Region r2) {

						final Form form = Form.NFD;

						final String n1 = Normalizer.normalize(r1.getLabel(),
								form);
						final String n2 = Normalizer.normalize(r2.getLabel(),
								form);

						return n1.compareTo(n2);
					}
				});

		sortedSet.addAll(dsRegions.values());

		// Author: Mauricio (begin)

		final Map<Integer, fr.univmobile.backend.core.Poi> allPois = pois
				.getAllBy(Integer.class, "uid");
		final List<fr.univmobile.backend.core.Poi> myPois = new ArrayList<fr.univmobile.backend.core.Poi>();
		for (final Integer uid : new TreeSet<Integer>(allPois.keySet())) {
			myPois.add(allPois.get(uid));
		}

		// Author: Mauricio (end)

		for (final fr.univmobile.backend.core.Region dsRegion : sortedSet) {

			final String regionId = dsRegion.getUid(); // REGION.ID ==
														// DS_REGION.UID!

			int poiCount = 0;

			// Author: Mauricio (begin)

			fr.univmobile.backend.core.University[] universities = dsRegion
					.getUniversities();

			if (regions.isNullByUid(regionId)) {

				poiCount = 0;

			} else {

				for (fr.univmobile.backend.core.Poi p : myPois) {
					final fr.univmobile.backend.core.Poi dsPoi = pois
							.getByUid(p.getUid());

					if (universities.length > 0) {
						for (int j = 0; j < universities.length; j++)
							if (dsPoi.getUniversityIds().length > 0)
								if (universities[j].getId().equals(
										dsPoi.getUniversityIds()[0]))
									poiCount++;
					}

				}
			}

			// Author: Mauricio (end)

			final String url = filterURL(dsRegion.getUrl());

			regionsArray[i] = DataBeans //
					.instantiate(MutableRegion.class) //
					.setId(regionId) //
					.setLabel(dsRegion.getLabel()) //
					.setUrl(url) //
					.setPoiCount(poiCount) //
					.setPoisUrl(url + "/pois");

			++i;
		}

		return regionsArray;
	}

	@Override
	public University[] getUniversitiesByRegion(final String regionId)
			throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getUniversitiesByRegion():" + regionId + "...");
		}

		final fr.univmobile.backend.core.Region region = regions
				.getByUid(regionId);

		final fr.univmobile.backend.core.University[] dsUniversities//
		= region.getUniversities();

		final University[] universities = new University[dsUniversities.length];

		int i = 0;

		final Set<fr.univmobile.backend.core.University> sortedSet = new TreeSet<fr.univmobile.backend.core.University>(
				new Comparator<fr.univmobile.backend.core.University>() {

					@Override
					public int compare(
							final fr.univmobile.backend.core.University u1,
							final fr.univmobile.backend.core.University u2) {

						final Form form = Form.NFD;

						final String n1 = Normalizer.normalize(u1.getTitle(),
								form);
						final String n2 = Normalizer.normalize(u2.getTitle(),
								form);

						return n1.compareTo(n2);
					}
				});

		sortedSet.addAll(Arrays.asList(dsUniversities));

		final String url = filterURL(region.getUrl());

		for (final fr.univmobile.backend.core.University dsUniversity : sortedSet) {

			final String universityId = dsUniversity.getId();

			final int poiCount;

			if (regions.isNullByUid(regionId)) {
				poiCount = 0;
			} else {
				poiCount = regions.getByUid(regionId).getUniversities().length;
			}

			universities[i] = DataBeans //
					.instantiate(MutableUniversity.class) //
					.setId(universityId) //
					.setTitle(dsUniversity.getTitle()) //
					.setConfigUrl(url + "/" + universityId) //
					.setPoiCount(poiCount) //
					.setPoisUrl(url + "/" + universityId + "/pois") //
					.setShibbolethIdentityProvider(
							dsUniversity.getShibbolethIdentityProvider());

			++i;
		}

		return universities;
	}

	private interface MutableRegion extends Region {

		MutableRegion setId(String id);

		MutableRegion setLabel(String label);

		MutableRegion setUrl(String url);

		MutableRegion setPoiCount(int poiCount);

		MutableRegion setPoisUrl(String poisUrl);
	}

	private interface MutableUniversity extends University {

		MutableUniversity setId(String id);

		MutableUniversity setTitle(String title);

		MutableUniversity setConfigUrl(String configUrl);

		MutableUniversity setPoiCount(int poiCount);

		MutableUniversity setPoisUrl(String poisUrl);

		MutableUniversity setShibbolethIdentityProvider(
				@Nullable String shibbolethIdentityProvider);
	}
}
