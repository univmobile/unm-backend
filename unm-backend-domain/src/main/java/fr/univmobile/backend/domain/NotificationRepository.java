package fr.univmobile.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends
		JpaRepository<Notification, Long>, NotificationRepositoryCustom {

}
