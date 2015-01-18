package fr.univmobile.backend.domain;

import java.util.Date;
import java.util.List;

public interface NotificationRepositoryCustom {

	List<Notification> notificationList(Integer limit, Date date);
}
