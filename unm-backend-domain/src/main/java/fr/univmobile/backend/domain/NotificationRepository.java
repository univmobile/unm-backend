package fr.univmobile.backend.domain;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;


public interface NotificationRepository extends	JpaRepository<Notification, Long>, NotificationRepositoryCustom {
	
	@Query("Select n from Notification n where (n.university = :universityId or n.university = null) order by n.createdOn desc")
	Page<News> findNotificationsForUniversity(@Param("universityId") University universityId, Pageable pageable);
	
	@Query("Select n from Notification n where (n.university = :universityId or n.university = null) and n.createdOn > :since order by n.createdOn desc")
	Page<News> findNotificationsForUniversitySince(@Param("universityId") University universityId, @Param("since") @DateTimeFormat(iso = ISO.DATE_TIME) Date since, Pageable pageable);

	Page<Feed> findByUniversityOrderByCreatedOnDesc(@Param("universityId")University universityId, Pageable pageable);
	
	Page<Feed> findByOrderByCreatedOnDesc(Pageable pageable);
}
