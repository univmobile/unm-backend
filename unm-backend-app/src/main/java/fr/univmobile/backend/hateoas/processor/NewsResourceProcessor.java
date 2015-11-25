package fr.univmobile.backend.hateoas.processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

import fr.univmobile.backend.domain.News;

public class NewsResourceProcessor implements ResourceProcessor<Resource<News>> {

    @Value("${newsDefaultImageUrl}")
    private String newsDefaultImageUrl;

	@Override
	public Resource<News> process(Resource<News> resource) {
		News news = resource.getContent();
		if (news.getImageUrl() == null || news.getImageUrl().trim().isEmpty()) {
			news.setImageUrl(newsDefaultImageUrl);
		}
		return resource;
	}

}
