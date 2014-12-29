package fr.univmobile.backend.api;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.json.AbstractJSONController;

@Controller
@RequestMapping("pois/nearest")
public class NearestPoisJSONController extends AbstractJSONController {

	@Autowired
	PoiRepository poiRepository;

	@Value("${nearestMaxDistanceInMeters}")
	private Double nearestMaxDistanceInMeters;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Poi> get(@RequestParam(value = "lat", required = false) Double lat,
			@RequestParam(value = "lng", required = false) Double lng,
			HttpServletResponse response) {

		List<Poi> result;

		if (getDelegationUser().isSuperAdmin()) {

			result = new ArrayList<Poi>();

			Iterable<Poi> allPois = poiRepository.findAll();

			for (Poi p : allPois)
				if (p.nearestPoi(lat, lng, nearestMaxDistanceInMeters))
					result.add(p);
		} else {
			result = poiRepository.findByUniversity(getDelegationUser()
					.getUniversity());
		}

		return result;
	}

	@Override
	public String actionJSON(String baseURL) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}