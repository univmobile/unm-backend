package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
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
		List<Region> regions = regionRepository.findAll();
		setAttribute("regions", regions);
		return new View("regions.jsp");
	}
}
