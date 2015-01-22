package fr.univmobile.backend.api;

import fr.univmobile.backend.domain.Notification;
import fr.univmobile.backend.domain.NotificationRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.backend.hateoas.assembler.NotificationStatusAssembler;
import fr.univmobile.backend.hateoas.resource.NotificationStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	NotificationStatusAssembler notificationStatusAssembler;

	@Autowired
	UserRepository userRepository;

	@Value("${notification.days}")
	int days = 0;

	@ResponseBody
	@RequestMapping(value = { "/recent" }, method = RequestMethod.GET)
	List<Notification> recent(
			@RequestParam(required = false, value = "limit") Integer limit,
			@RequestParam(required = false, value = "since", defaultValue = "currentdate") Date since) {

		return notificationRepository.notificationList(limit, since);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final CustomDateEditor dateEditor = new CustomDateEditor(df, true) {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				if ("currentdate".equals(text)) {
					Calendar cal = GregorianCalendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DAY_OF_YEAR, -days);
					setValue(cal.getTime());
				} else {
					super.setAsText(text);
				}
			}
		};
		binder.registerCustomEditor(Date.class, dateEditor);
	}

	@ResponseBody
	@RequestMapping(value = { "/unreadCount" }, method = RequestMethod.GET)
	public NotificationStatusResource unreadCount(@RequestParam(value = "userId") Long userId) {
		User user = userRepository.findOne(userId);
		NotificationStatus notificationStatus = null;
		if (user != null) {
			notificationStatus = new NotificationStatus();
			notificationStatus.unreadNotifications = notificationRepository.countCotificationList(user.getNotificationsReadDate());
			notificationStatus.user = user;
		}
		return notificationStatusAssembler.toResource(notificationStatus);
	}

	@ResponseBody
	@RequestMapping(value = { "/lastRead" }, method = RequestMethod.GET)
	public NotificationStatusResource lastRead(@RequestParam(value = "userId") Long userId, @RequestParam(value = "notificationId") Long notificationId) {
		Notification notification = notificationRepository.findOne(notificationId);
		User user = userRepository.findOne(userId);
		NotificationStatus notificationStatus = null;
		if (user != null && notification != null){
			user.setNotificationsReadDate(notification.getUpdatedOn());
			userRepository.save(user);
			notificationStatus = new NotificationStatus();
			notificationStatus.user = user;
		}
		return notificationStatusAssembler.toResource(notificationStatus);
	}

	public class NotificationStatus {
		public User user;
		public Long unreadNotifications;
	}
}