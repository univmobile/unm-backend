package fr.univmobile.backend.client;

import javax.annotation.Nullable;

public interface University {

	String getId();

	String getTitle();
	
	String getConfigUrl();
	
	int getPoiCount();
	
	String getPoisUrl();
	
	@Nullable
	String getShibbolethIdentityProvider();
}
