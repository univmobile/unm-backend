package fr.univmobile.backend.jobs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;

public class ApiParisUtils {
	
    @Value("${api.paris.activities.url:http://localhost:8082/paris.html}")
    private String API_PARIS_ACTIVITIES_URL;

    @Value("${api.paris.parameter.token:ba14fa26b212ad9e3867312a5abb1a3749dc8e122e204de026709289033047f7}")
    private String API_PARIS_PARAMETER_TOKEN;
    
    @Value("${api.paris.parameter.created:0}")
    private String API_PARIS_PARAMETER_CREATED;
    
    @Value("${api.paris.parameter.start:0}")
    private String API_PARIS_PARAMETER_START;

    @Value("${api.paris.parameter.end:0}")
    private String API_PARIS_PARAMETER_END;

    @Value("${api.paris.parameter.offset:0}")
    private String API_PARIS_PARAMETER_OFFSET;

    @Value("${api.paris.parameter.limit:100}")
    private String API_PARIS_PARAMETER_LIMIT;	
	
    @Value("${api.paris.university_id:5}")
    private String API_PARIS_UNIVERSITY_ID;	

    @Autowired
	private PoiRepository poiRepository;
	 
	@Autowired
	private CategoryRepository categoryRepository;
	 
	@Autowired
	private UniversityRepository universityRepository;
	 
	@Autowired
	private RegionRepository regionRepository;

	private static final Log log = LogFactory.getLog(ApiParisUtils.class);

	public boolean apiCategoryIsAmongSelected(String selectedCategories, String apiCategory) {
		
		if (selectedCategories.endsWith("," + apiCategory)) {
			return true;
		}
		
		if (selectedCategories.startsWith("cid=" + apiCategory + ",")) {
			return true;
		}
		
		if (selectedCategories.contains("," + apiCategory + ",")) {
			return true;
		}
		
		if (selectedCategories.equalsIgnoreCase("cid=" + apiCategory)) {
			return true;
		}
		
		return false;
	}
	

	public void getEvents(String start, String end, String limit, String offset) {

		try {

			String cid = new String("");

			for (Category c : categoryRepository.findAllWhereApiParisIdIsNotNullAndActive()) {
				if (c.getApiParisId() != null && c.isActive())
					cid += String.valueOf(c.getApiParisId()) + ",";
			}			
			
			if (cid.length() > 0) {
				cid = "cid=" + cid.substring(0, cid.length() - 1);
			}
			
			String url = API_PARIS_ACTIVITIES_URL + "?token=" +  API_PARIS_PARAMETER_TOKEN + "&created=" + API_PARIS_PARAMETER_CREATED
					+ "&start=" + API_PARIS_PARAMETER_START + "&end=" + API_PARIS_PARAMETER_END + "&offset="
					+ API_PARIS_PARAMETER_OFFSET + "&limit=" + API_PARIS_PARAMETER_LIMIT
					+ cid ;

			
			HttpClient client = HttpClientBuilder.create().build();
			
			HttpGet get = new HttpGet(url);
			
		
			
			HttpResponse response = client.execute(get);
			
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			JsonObject jsonObject = JsonObject.readFrom(rd);

			JsonArray dataArray = JsonArray.readFrom(jsonObject.get("data")
					.toString());

			int size = dataArray.size();

			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

			
			for (int i = 0; i < size; i++) {

				JsonObject dataObject = dataArray.get(i).asObject();

				Long idActivity = Long.parseLong(dataObject.get("idactivites")
						.toString());
				String name = dataObject.get("nom").toString();

				String description = dataObject.get("description").toString();
				String smallDescription = dataObject.get("small_description")
						.toString();
				String place = dataObject.get("lieu").toString();
				String address = dataObject.get("adresse").toString();
				String zipCode = dataObject.get("zipcode").toString();
				String city = dataObject.get("city").toString();
				Double lat = Double.parseDouble(dataObject.get("lat")
						.toString());
				Double lng = Double.parseDouble(dataObject.get("lon")
						.toString());
				
				String discipline;
				
				if (dataObject.get("discipline") != null) {
					discipline = dataObject.get("discipline").toString();
				} else {
					discipline = null;
				}
					
				
				JsonArray ocurrences = dataObject.get("occurrences").asArray();
				
				
				Date expDate;
								
				if (ocurrences != null && ocurrences.size() > 0) {
					expDate = sdf.parse(ocurrences.get(0).asObject().get("jour").asString());
				} else {
					expDate = null;
				}


				
				JsonArray rubriques = dataObject.get("rubriques").asArray();
				List<String> categories = new ArrayList<String>();
				
				String categoryId = null;
				Category c = null;
				
				if (rubriques != null) {
					for (int j = 0; j < rubriques.size(); j++) {
						categories.add(rubriques.get(j).asObject().get("rubrique").toString());
						
						categoryId = rubriques.get(j).asObject().get("id").toString();
						if (apiCategoryIsAmongSelected(cid, categoryId) ) {
							c = categoryRepository.findByApiParisId(Long.parseLong(categoryId));
							break;
						}
					}
				}
				
				
				if (c != null) {

					Poi poi = poiRepository.findByExternalId(idActivity);
					if (poi == null)
						poi = new Poi();

				
					poi.setName(name);
					poi.setDescription(smallDescription);
					poi.setLat(lat);
					poi.setLng(lng);
					poi.setAddress(address);
					poi.setZipcode(zipCode);
					poi.setCity(city);
					poi.setFloor(place);
					poi.setExpDate(expDate);
					poi.setCreatedOn(new Date());
					poi.setUpdatedOn(new Date());
					poi.setExternalId(idActivity);
					poi.setDisciplines(discipline);
					poi.setCategory(c);
				
					University u = universityRepository.findOne(Long.parseLong(API_PARIS_UNIVERSITY_ID));
					poi.setUniversity(u);
				
					try {
						poiRepository.save(poi);
					} catch (Exception e) {
						System.out.println("Exception: " + e.getMessage());
					}
				
				}
				
			}
			
			
			Date currentDate = new Date();
			for (Poi poi : poiRepository.findAll()) {
				if (poi.getExpDate() != null && poi.getExpDate().before(currentDate))
					poiRepository.delete(poi);
			}

		} catch (Exception e) {
			log.error("Exception occured trying to get the ApiParis events", e);
		}
	}

}
