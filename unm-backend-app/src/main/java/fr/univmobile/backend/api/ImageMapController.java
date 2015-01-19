package fr.univmobile.backend.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.univmobile.backend.hateoas.assembler.ImageMapDataResourceAssembler;
import fr.univmobile.backend.hateoas.resource.ImageMapDataResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.univmobile.backend.domain.ImageMap;
import fr.univmobile.backend.domain.ImageMapRepository;
import fr.univmobile.backend.domain.Poi;

@Controller
@RequestMapping(ImageMapController.IMAGE_MAP_WITH_POI_SELECTED_PATH)
public class ImageMapController {

	public static final String IMAGE_MAP_WITH_POI_SELECTED_PATH = "/imagemap";
	public static final String IMAGE_MAP_WITH_POI_SELECTED_POI_ID_PARAM = "poi";
	public static final String IMAGE_MAP_WITH_POI_SELECTED_IMAGE_MAP_ID_PARAM = "im";
	
	@Autowired
	ImageMapRepository imageMapRepository;

	@Autowired
	ImageMapDataResourceAssembler imageMapDataResourceAssembler;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ImageMapDataResource get(@RequestParam(IMAGE_MAP_WITH_POI_SELECTED_IMAGE_MAP_ID_PARAM) Long imageMapId, @RequestParam(value = IMAGE_MAP_WITH_POI_SELECTED_POI_ID_PARAM, required = false) Long selectedPoiId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ImageMapData data = new ImageMapData();
		data.imageMap = imageMapRepository.findOne(imageMapId);
		
		if (data.imageMap == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		for (Poi poi : data.imageMap.getPois()) {
			if (poi.getId() == selectedPoiId) {
				data.selectedPoi = poi;
			}
		}

		return imageMapDataResourceAssembler.toResource(data);
	}

	public static String buildImageMapWithSelectedPoiUrl(String apiPath, Long imageMapId, Long selectedPoiId) {
		return String.format("%s%s?%s=%d&%s=%d", apiPath, ImageMapController.IMAGE_MAP_WITH_POI_SELECTED_PATH, IMAGE_MAP_WITH_POI_SELECTED_IMAGE_MAP_ID_PARAM, imageMapId, IMAGE_MAP_WITH_POI_SELECTED_POI_ID_PARAM, selectedPoiId);
	}
	
	public class ImageMapData {
		
		private ImageMap imageMap;
		private Poi selectedPoi;
		
		public ImageMap getImageMap() {
			return imageMap;
		}
		public void setImageMap(ImageMap imageMap) {
			this.imageMap = imageMap;
		}
		public Poi getSelectedPoi() {
			return selectedPoi;
		}
		public void setSelectedPoi(Poi selectedPoi) {
			this.selectedPoi = selectedPoi;
		}
		
	}
}
