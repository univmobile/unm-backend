package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "regions", "regions/" })
public class RegionsController extends AbstractBackendController {

	public RegionsController(final RegionRepository regionRepository) {
		this.regionRepository = checkNotNull(regionRepository,
				"regionRepository");
	}

	private RegionRepository regionRepository;

	@Override
	public View action() {

		// 1. UPDATE?

		final UpdateRegions ur = getHttpInputs(UpdateRegions.class);
		if (ur.isHttpValid()) {

			Region region1 = regionRepository.findOne(new Long(1));
			Region region2 = regionRepository.findOne(new Long(2));
			Region region3 = regionRepository.findOne(new Long(3));

			if (region1 != null && ur.region_1() != null && !region1.getLabel().equals(ur.region_1())) {
				region1.setLabel(ur.region_1());
				regionRepository.save(region1);
			}

			if (region2 != null && ur.region_2() != null && !region2.getLabel().equals(ur.region_2())) {
				region2.setLabel(ur.region_2());
				regionRepository.save(region2);
			}

			if (region3 != null && ur.region_3() != null && !region3.getLabel().equals(ur.region_3())) {
				region3.setLabel(ur.region_3());
				regionRepository.save(region3);
			}
		}

		// 2. VIEW

		List<Region> regions = regionRepository.findAll();
		setAttribute("regions", regions);

		// 3. END

		return new View("regions.jsp");
	}

	@HttpMethods("POST")
	interface UpdateRegions extends HttpInputs {

		@HttpParameter(trim = true)
		String region_1();

		@HttpParameter(trim = true)
		String region_2();

		@HttpParameter(trim = true)
		String region_3();
	}
}
