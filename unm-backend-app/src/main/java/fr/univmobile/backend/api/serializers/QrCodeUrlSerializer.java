package fr.univmobile.backend.api.serializers;

import org.springframework.stereotype.Component;

@Component
public class QrCodeUrlSerializer extends StringUrlFillerSerializer {

	public final static String BASE_URL_PARAM_KEY = "qrCodesBaseUrl";

	@Override
	protected String getBaseUrlParamKey() {
		return BASE_URL_PARAM_KEY;
	}
	
}
