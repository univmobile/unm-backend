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

	private boolean updateRegionLabel(Long regionId, String newLabel){
		if (newLabel != null && !newLabel.isEmpty()){
			Region region = regionRepository.findOne(new Long(regionId));
			if (region != null && !region.getLabel().equals(newLabel)) {
				region.setLabel(newLabel);
				regionRepository.save(region);
				return true;
			}
		}
		return false;
	}

	@Override
	public View action() {

		// 1. UPDATE?

		final UpdateRegions ur = getHttpInputs(UpdateRegions.class);
		if (ur.isHttpValid()) {
			updateRegionLabel(new Long(1), ur.region_1());
			updateRegionLabel(new Long(2), ur.region_2());
			updateRegionLabel(new Long(3), ur.region_3());
			updateRegionLabel(new Long(4), ur.region_4());
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

		@HttpParameter(trim = true)
		String region_4();
	}
}
