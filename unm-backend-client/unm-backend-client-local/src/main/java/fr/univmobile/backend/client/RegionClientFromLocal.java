package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.DataBeans;

public class RegionClientFromLocal implements RegionClient {

	@Inject
	public RegionClientFromLocal(final RegionDataSource dataSource) {

		this.dataSource = checkNotNull(dataSource, "dataSource");
	}

	private final RegionDataSource dataSource;

	@Override
	public Region[] getRegions() throws IOException {

		final Map<String, fr.univmobile.backend.core.Region> dsRegions //
		= dataSource.getAllBy("uid");

		final Region[] regions = new Region[dsRegions.size()];

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

		for (fr.univmobile.backend.core.Region dsRegion : sortedSet) {

			regions[i] = DataBeans //
					.instantiate(MutableRegion.class) //
					.setId(dsRegion.getUid()) // REGION.ID == DS_REGION.UID!
					.setLabel(dsRegion.getLabel()) //
					.setUrl(dsRegion.getUrl());

			++i;
		}

		return regions;
	}

	private interface MutableRegion extends Region {

		MutableRegion setId(String id);

		MutableRegion setLabel(String label);

		MutableRegion setUrl(String url);
	}
}
