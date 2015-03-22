package fr.univmobile.backend.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;

@Controller
@RequestMapping("/regionsp")
public class RegionController {

	private static final Log log = LogFactory.getLog(RegionController.class);


	@Value("${baseURL}")
	private String baseUrl;

	@Autowired
	RegionRepository regionRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public RegionsData get(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RegionsData data = new RegionsData();
		
		data.setUrl(composeUrl(baseUrl, request.getRequestURI()));
		data.setRegions(regionRepository.findAllByOrderByNameAsc());
		
		return data;
	}

	@RequestMapping(method = RequestMethod.POST, value = "modifyLabels")
	public String modifyLabels(@RequestParam Map<String,String> pairs) throws IOException {
		for (String id : pairs.keySet()){
			try {
				updateRegionLabel(Long.valueOf(id), pairs.get(id));
			} catch (Exception e) {
				log.error("Error while updating region label", e);
			}
		}
		return "redirect:/regions/";
	}

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


	
	public class RegionsData {

		private String url;
		private List<Region> regions;

		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public List<Region> getRegions() {
			return regions;
		}
		public void setRegions(List<Region> regions) {
			this.regions = regions;
		}
		
	}
	
	private String composeUrl(String baseUrl, String path) {
		String tmpBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 2) : baseUrl;
		return String.format("%s%s", tmpBaseUrl, path);
	}
}
