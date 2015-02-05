package fr.univmobile.backend.api.serializers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class PoiMixIn {
	
	@JsonSerialize(using = QrCodeUrlSerializer.class)
	abstract String getQrCode();
	
}
