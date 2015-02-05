package fr.univmobile.backend.hateoas.processor;

import fr.univmobile.backend.domain.Feed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class FeedResourceProcessor implements ResourceProcessor<Resource<Feed>>{

    private static String UNIVERSITIES_PATH = "api/universities/";

    @Value("${baseURL}")
    private String baseUrl;

    @Override
    public Resource<Feed> process(Resource<Feed> resource) {
        Feed feed = resource.getContent();
        if (feed.getUniversity() != null){
            resource.add(new Link(baseUrl + UNIVERSITIES_PATH + feed.getUniversity().getId(), "universityLink"));
        }
        return resource;
    }
}
