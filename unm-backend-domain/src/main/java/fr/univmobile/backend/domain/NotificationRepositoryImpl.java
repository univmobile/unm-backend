package fr.univmobile.backend.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

//	@Autowired
	//TODO fix this bean
	private EntityManager em;

	@Override
	public List<Notification> notificationList(Integer limit, Date date) {
		Query query = em.createNamedQuery("Notification.searchByCreatedDate")
				.setParameter("date", date);
		if (limit != null) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}
}