package fr.univmobile.backend.hateoas.processor;

import fr.univmobile.backend.domain.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class NotificationResourceProcessor implements ResourceProcessor<Resource<Notification>> {

    private static String UNIVERSITIES_PATH = "api/universities/";

    @Value("${baseURL}")
    private String baseUrl;

    @Override
    public Resource<Notification> process(Resource<Notification> resource) {
        Notification notification = resource.getContent();
        if (notification.getUniversity() != null){
            resource.add(new Link(baseUrl + UNIVERSITIES_PATH + notification.getUniversity().getId(), "universityLink"));
        }
        return resource;
    }
}
