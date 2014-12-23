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

		Region ile_de_france;
		Region bretagne;
		Region unrpcl;

		ile_de_france = regionRepository.findByLabel("ile_de_france");
		bretagne = regionRepository.findByLabel("bretagne");
		unrpcl = regionRepository.findByLabel("unrpcl");
		
		// 1. UPDATE?

		final UpdateRegions ur = getHttpInputs(UpdateRegions.class);

		if (ur.isHttpValid()) {

			if (!ile_de_france.getLabel().equals(ur.region_ile_de_france())) {
				ile_de_france.setLabel(ur.region_ile_de_france());
				regionRepository.save(ile_de_france);
			}

			if (!bretagne.getLabel().equals(ur.region_bretagne())) {
				bretagne.setLabel(ur.region_bretagne());
				regionRepository.save(bretagne);
			}

			if (!unrpcl.getLabel().equals(ur.region_unrpcl())) {
				unrpcl.setLabel(ur.region_unrpcl());
				regionRepository.save(unrpcl);
			}
		}

		// 2. VIEW

		Iterable<Region> allRegions = regionRepository.findAll();

		List<Region> regions = new ArrayList<Region>();

		for (Region r : allRegions)
			regions.add(r);

		setAttribute("regions", regions);

		// 3. END

		return new View("regions.jsp");
	}

	@HttpMethods("POST")
	interface UpdateRegions extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
		String region_ile_de_france();

		@HttpRequired
		@HttpParameter(trim = true)
		String region_bretagne();

		@HttpRequired
		@HttpParameter(trim = true)
		String region_unrpcl();
	}
}
