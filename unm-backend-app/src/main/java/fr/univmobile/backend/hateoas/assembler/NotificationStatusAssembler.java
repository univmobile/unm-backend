package fr.univmobile.backend.hateoas.assembler;

import fr.univmobile.backend.api.NotificationController;
import fr.univmobile.backend.hateoas.resource.NotificationStatusResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;


public class NotificationStatusAssembler extends ResourceAssemblerSupport<NotificationController.NotificationStatus, NotificationStatusResource> {

    private static final String NOTIFICATIONS_PATH = "api/notifications/";
    private static final String RECENT_WITH_DATE_PATH = "/recent?since=";
    private static final String USERS_PATH = "api/users/";

    @Value("${baseURL}")
    private String baseUrl;

    public NotificationStatusAssembler() {
        super(NotificationController.class, NotificationStatusResource.class);
    }

    @Override
    public NotificationStatusResource toResource(NotificationController.NotificationStatus notificationStatus){
        NotificationStatusResource notificationStatusResource = null;
        if (notificationStatus != null){
            notificationStatusResource = new NotificationStatusResource();
            if (notificationStatus.unreadNotifications != null){
                notificationStatusResource.unreadNotifications = notificationStatus.unreadNotifications;
            }
            notificationStatusResource.lastNotificationReadDate = notificationStatus.user.getNotificationsReadDate();

            notificationStatusResource.add(new Link(baseUrl + USERS_PATH + notificationStatus.user.getId(), "user"));
//            notificationStatusResource.add(new Link(baseUrl + NOTIFICATIONS_PATH + RECENT_WITH_DATE_PATH + notificationStatus.user.getNotificationsReadDate(), "unreadNotifications"));
        }
        return notificationStatusResource;
    }
}
