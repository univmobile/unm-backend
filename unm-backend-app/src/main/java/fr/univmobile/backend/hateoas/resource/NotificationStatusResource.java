package fr.univmobile.backend.hateoas.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.Date;

public class NotificationStatusResource extends ResourceSupport {
    public Long unreadNotifications;
    public Date lastNotificationReadDate;
}
