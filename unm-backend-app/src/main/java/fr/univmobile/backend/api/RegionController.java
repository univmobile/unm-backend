package fr.univmobile.backend.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;

@Controller
@RequestMapping("/regionsp")
public class RegionController {

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
