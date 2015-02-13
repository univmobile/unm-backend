package fr.univmobile.backend.jobs.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

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
	
	PoiRepository poiRepository;
	CategoryRepository categoryRepository;
	UniversityRepository universityRepository;
	RegionRepository regionRepository;
	
	public ApiParisUtils(PoiRepository poiRepository,
			CategoryRepository categoryRepository,
			UniversityRepository universityRepository,
			RegionRepository regionRepository) {
		
		this.poiRepository = poiRepository;
		this.categoryRepository = categoryRepository;
		this.universityRepository = universityRepository;
		this.regionRepository = regionRepository;
		
	}

	private static final Log log = LogFactory.getLog(ApiParisUtils.class);

	// HTTP POST request
	public void getEvents(String start, String end, String limit, String offset) {

		try {

			String url = "https://api.paris.fr/api/data/1.4/QueFaire/get_activities/";

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("token","ba14fa26b212ad9e3867312a5abb1a3749dc8e122e204de026709289033047f7"));
			urlParameters.add(new BasicNameValuePair("cid", "3,42,25"));
			urlParameters.add(new BasicNameValuePair("tag", "3,42,25"));
			urlParameters.add(new BasicNameValuePair("created", "0"));
			urlParameters.add(new BasicNameValuePair("start", start));
			urlParameters.add(new BasicNameValuePair("end", end));
			urlParameters.add(new BasicNameValuePair("limit", limit));
			urlParameters.add(new BasicNameValuePair("offset", offset));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + post.getEntity());
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			JsonObject jsonObject = JsonObject.readFrom(rd);

			System.out.println(jsonObject.toString());

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

				JsonArray ocurrences = dataObject.get("occurrences").asArray();
				Date expDate = sdf.parse(ocurrences.get(0).asObject()
						.get("jour").asString());

				// String discipline = dataObject.get("discipline").toString();

				JsonArray rubriques = dataObject.get("rubriques").asArray();

				List<String> categories = new ArrayList<String>();
				for (int j = 0; j < rubriques.size(); j++) {
					categories.add(rubriques.get(j).asObject().get("rubrique")
							.toString());
				}

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

				Category c = categoryRepository.findByName("API Paris");
				if (c == null) {
					c = new Category();
					c.setName("API Paris");
					categoryRepository.save(c);
				}
				Region r = regionRepository.findByLabel("region_temp");
				if (r == null) {
					r = new Region();
					r.setName("RegionTemp");
					r.setLabel("region_temp");
					r.setUrl("");
					regionRepository.save(r);
				}
				University u = universityRepository
						.findByTitle("UniversityApiParis");
				if (u == null) {
					u = new University();
					u.setTitle("UniversityApiParis");
					u.setRegion(r);
					universityRepository.save(u);
				}

				poi.setCategory(c);
				poi.setUniversity(u);

				System.out.println(poi.getName());

				poiRepository.save(poi);
			}

			Date currentDate = new Date();
			for (Poi poi : poiRepository.findAll()) {
				if (poi.getExpDate().before(currentDate))
					poiRepository.delete(poi);
			}

		} catch (Exception e) {
			log.error("Exception occured trying to get the ApiParis events", e);
		}
	}

}
