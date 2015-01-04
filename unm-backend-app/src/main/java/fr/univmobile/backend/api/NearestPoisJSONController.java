package fr.univmobile.backend.api;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.User;

@Controller
@RequestMapping("pois/nearest")
public class NearestPoisJSONController {

	@Autowired
	PoiRepository poiRepository;

	@Value("${nearestMaxDistanceInMeters}")
	private Double nearestMaxDistanceInMeters;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Poi> get(@RequestParam(value = "lat", required = false) Double lat,
			@RequestParam(value = "lng", required = false) Double lng,
			HttpServletRequest request,
			HttpServletResponse response) {

		User currentUser = getCurrentUser(request);
		List<Poi> result = new LinkedList<Poi>();
		List<Poi> allPois;

		if (currentUser == null || currentUser.isSuperAdmin()) {
			allPois = poiRepository.findByCategory_LegacyNotLike(Category.getImageMapsLegacy() + '%');
		} else {
			allPois = poiRepository.findByUniversityAndCategory_LegacyNotLike(currentUser
					.getUniversity(), Category.getImageMapsLegacy() + '%');
		}

		for (Poi p : allPois)
			if (p.isNear(lat, lng, nearestMaxDistanceInMeters))
				result.add(p);

		return result;
	}

	private User getCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}

}