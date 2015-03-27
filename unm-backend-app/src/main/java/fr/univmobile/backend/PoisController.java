package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.User;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "pois", "pois/" })
public class PoisController extends AbstractBackendController {

	public PoisController(final PoiRepository poiRepository,
			final RegionRepository regionRepository) {
		this.poiRepository = checkNotNull(poiRepository, "poiRepository");
		this.regionRepository = checkNotNull(regionRepository,
				"regionRepoitory");

	}

	private PoiRepository poiRepository;
	private RegionRepository regionRepository;

	@Override
	public View action() {

		User dUser = getDelegationUser();

		setAttribute("user", dUser);

		// 1. POIS DATA

		List<Poi> allPois = new ArrayList<Poi>();
		final Search search = getHttpInputs(Search.class);
		String query = (search.q() != null) ? search.q() : "";
		if (dUser.isSuperAdmin()) {
			allPois = poiRepository.findByParentIsNullAndRootCategoryAndNameAndDescription(Category.getPlansLegacy(), query);
		} else {
			allPois = poiRepository.findByParentIsNullAndRootCategoryAndUniversityAndNameAndDescription(Category.getPlansLegacy(), dUser.getUniversity(), query);
		}

		List<PoiGroup> poiGroups = new ArrayList<PoiGroup>();

		Iterable<Region> allRegions = regionRepository.findAll();

		for (Region r : allRegions) {
			PoiGroup poiGroup = instantiate(PoiGroup.class);
			poiGroup.setRegion(r);
			List<Poi> pois = new ArrayList<Poi>();
			for (Poi p : allPois)
				for (University u : r.getUniversities())
					if (u.getId() == p.getUniversity().getId())
						pois.add(p);
			poiGroup.setPois(pois);
			poiGroups.add(poiGroup);
		}

		setAttribute("poiGroups", poiGroups);

		// 2. POIS INFO

		int resultCount = 0;

		for (final PoiGroup poiGroup : poiGroups) {
			resultCount += poiGroup.getPois().size();
		}

		final PoisInfo poisInfo = instantiate(PoisInfo.class) //
				.setCount(resultCount) //
				.setContext("POIS de plus haut niveau") //
				.setResultCount(resultCount); //

		setAttribute("poisInfo", poisInfo);

		// 3. END

		return new View("pois.jsp");
	}
}

interface PoiGroup {

	Region getRegion();

	PoiGroup setRegion(Region region);

	List<Poi> getPois();

	PoiGroup setPois(List<Poi> pois);

}

interface PoisInfo {

	/**
	 * Total count of POIs in the DataBase.
	 */
	int getCount();

	PoisInfo setCount(int count);

	/**
	 * e.g. "POIs de plus haut niveau"
	 */
	@Nullable
	String getContext();

	PoisInfo setContext(String context);

	/**
	 * Count of POIs returned by the search.
	 */
	int getResultCount();

	PoisInfo setResultCount(int count);
}

interface Search extends HttpInputs {
	@HttpParameter
	String q();
}
