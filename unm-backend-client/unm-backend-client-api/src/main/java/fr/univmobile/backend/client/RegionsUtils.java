package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.Nullable;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.University;

public abstract class RegionsUtils {

	@Nullable
	public static Region getRegionById(final RegionClient regions,
			final String id) throws IOException {

		checkNotNull(regions, "regions");
		checkNotNull(id, "id");

		for (final Region region : regions.getRegions()) {

			if (id.equals(region.getId())) {

				return region;
			}
		}

		return null;
	}

	@Nullable
	public static University getUniversityById(final RegionClient regions,
			final String id) throws IOException {

		checkNotNull(regions, "regions");
		checkNotNull(id, "id");

		for (final Region region : regions.getRegions()) {

			for (final University university : //
			regions.getUniversitiesByRegion(region.getId())) {

				if (id.equals(university.getId())) {

					return university;
				}
			}
		}

		return null;
	}
}
