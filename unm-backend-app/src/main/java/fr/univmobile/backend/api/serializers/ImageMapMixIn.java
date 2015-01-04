package fr.univmobile.backend.api.serializers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class ImageMapMixIn {
	
	@JsonSerialize(using = ImageMapUrlSerializer.class)
	abstract String getUrl();
	
}
