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
import org.springframework.beans.factory.annotation.Autowired;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;

public class ApiParisUtils {
	
	@Autowired
	PoiRepository poiRepository;
	
	private static final Log log = LogFactory.getLog(ApiParisUtils.class);

	// HTTP POST request
	public void getEvents() {
		
		try {

			String url = "https://api.paris.fr/api/data/1.4/QueFaire/get_activities/";
	
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);
	
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("token", "ba14fa26b212ad9e3867312a5abb1a3749dc8e122e204de026709289033047f7"));
			urlParameters.add(new BasicNameValuePair("cid", "3,42,25"));
			urlParameters.add(new BasicNameValuePair("tag", "3,42,25"));
			urlParameters.add(new BasicNameValuePair("created", "0"));
			urlParameters.add(new BasicNameValuePair("start", "0"));
			urlParameters.add(new BasicNameValuePair("end", "0"));
			urlParameters.add(new BasicNameValuePair("limit", "3"));
			urlParameters.add(new BasicNameValuePair("offset", "0"));
	
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
	
			HttpResponse response = client.execute(post);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + post.getEntity());
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
	
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	
			JsonObject jsonObject = JsonObject.readFrom(rd);
	
			JsonArray dataArray = JsonArray.readFrom(jsonObject.get("data").toString());
			
			int size = dataArray.size();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
			
			for (int i = 0; i < size; i ++) {
				
				JsonObject dataObject = dataArray.get(i).asObject();
				
				Long idActivity = Long.parseLong(dataObject.get("idactivites").toString());
				String name = dataObject.get("nom").toString();
				String description = dataObject.get("description").toString();
				String smallDescription = dataObject.get("small_description").toString();
				String place = dataObject.get("lieu").toString();
				String address = dataObject.get("adresse").toString();
				String zipCode = dataObject.get("zipcode").toString();
				String city = dataObject.get("city").toString();
				Double lat = Double.parseDouble(dataObject.get("lat").toString());
				Double lng = Double.parseDouble(dataObject.get("lon").toString());
				
				JsonArray ocurrences = dataObject.get("ocurrences").asArray();				
				Date expDate = sdf.parse(ocurrences.get(0).asObject().get("jour").asString());
				
				// String discipline = dataObject.get("discipline").toString();
				
				JsonArray rubriques = dataObject.get("rubriques").asArray();
				
				List<String> categories = new ArrayList<String>();
				for(int j = 0; j < rubriques.size(); j++) {
					categories.add(rubriques.get(j).asObject().get("rubrique").toString());
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
