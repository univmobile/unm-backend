package fr.univmobile.backend.domain;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Notification> notificationList(Integer limit, Date date) {
		Query query = em.createNamedQuery("Notification.searchByCreatedDate").setParameter("date", date);
		if (limit != null) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	@Override
	public Long countCotificationList(Date date) {
		Query query = em.createNamedQuery("Notification.countByCreatedDate").setParameter("date", date);
		return (Long)query.getSingleResult();
	}
}