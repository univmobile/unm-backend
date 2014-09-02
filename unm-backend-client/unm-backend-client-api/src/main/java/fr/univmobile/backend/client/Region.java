package fr.univmobile.backend.client;


public interface Region {

	String getId();

	String getLabel();

	String getUrl();
	
	int getPoiCount();
	
	String getPoisUrl();
}
