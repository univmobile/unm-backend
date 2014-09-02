package fr.univmobile.backend.client;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

/*
 *            "id":              "9120122",
 "url":             "http://localhost:8380/unm-backend-mock/json/comments/dandriana/9120122",
 "postedAt":        "2014-08-15 09:34:45.894+02:00",                                               
 "displayPostedAt": "",
 "source":   "UnivMobile",
 "author":{
 "username":    "dandriana",
 "displayName": "David Andriana",
 "timeZone":    "Central European Time Zone (UTC+01:00)",
 "lang":        "fr",
 "profileImage": {
 "url":"http://unpidf.univ-paris1.fr/wp-content/themes/wp-creativix/images/logos/1338991426.png"
 }
 },
 "coordinates":{
 "type": "Point",
 "lat":  "48.627078",
 "lng":  "2.431309"
 },
 "lang":"fr",
 "entities":{
 "hashtags":[
 {
 "indices": [28,34],
 "text":    "unpidf"
 }
 ],
 "urls":[
 {
 "indices":     [36,65],
 "url":         "http://unpidf.univ-paris1.fr/",
 "displayUrl":  "http://unpidf.univ-paris1.fr/",
 "expandedUrl": "http://unpidf.univ-paris1.fr/"
 }
 ]
 },
 "text":"Une application de l’UNPIdF #unpidf http://unpidf.univ-paris1.fr/"

 */
public interface Comment {

	String getId();

	String getUrl();

	DateTime getPostedAt();

	/**
	 * e.g. "15 août — 9 h 34"
	 */
	String getDisplayPostedAt();

	/**
	 * e.g. "Vendredi 15 août 2014, 9 h 34"
	 */
	String getDisplayFullPostedAt();

	/**
	 * e.g. "9 h 34"
	 */
	String getDisplayPostedAtTime();

	String getSource();

	String getAuthorUsername();

	String getAuthorDisplayName();

	@Nullable
	String getAuthorTimeZone();

	@Nullable
	String getAuthorLang();

	boolean isNullAuthorLang();
	
	@Nullable
	String getAuthorProfileImageUrl();

	@Nullable
	String getLatitude();

	@Nullable
	String getLongitude();

	@Nullable
	String getLang();

	String getText();

	Url[] getUrls();

	interface Entity {

		int[] getIndices();
	}
	
	interface Url extends Entity {
		
		String getUrl();
		
		String getDisplayUrl();
		
		String getExpandedUrl();
	}
	
	interface Hashtag extends Entity {
		
		String getText();
	}
}
