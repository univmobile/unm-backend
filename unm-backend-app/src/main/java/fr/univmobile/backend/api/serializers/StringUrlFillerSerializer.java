package fr.univmobile.backend.api.serializers;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public abstract class StringUrlFillerSerializer extends JsonSerializer<String> {
	
	@Autowired
	ServletContext servletContext;
	
	public StringUrlFillerSerializer() {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	} 
	
	@Override
	public void serialize(String relativeUrl, 
			JsonGenerator jsonGenerator, 
			SerializerProvider serializerProvider) 
					throws IOException, JsonProcessingException {
		
		String url;
		String baseUrl = getBaseUrl();
		if (baseUrl == null) {
			url = relativeUrl;
		} else {
			url = String.format("%s/%s", baseUrl, relativeUrl);
		}
		
		jsonGenerator.writeObject(url);
	}

	protected String getBaseUrl() {
		return servletContext.getInitParameter(getBaseUrlParamKey());
	}
	
	protected abstract String getBaseUrlParamKey();

}
