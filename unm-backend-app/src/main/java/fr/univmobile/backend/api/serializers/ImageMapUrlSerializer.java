package fr.univmobile.backend.api.serializers;

import org.springframework.stereotype.Component;

@Component
public class ImageMapUrlSerializer extends StringUrlFillerSerializer {

	public final static String BASE_URL_PARAM_KEY = "imageMapsBaseUrl";

	@Override
	protected String getBaseUrlParamKey() {
		return BASE_URL_PARAM_KEY;
	}
	
}
