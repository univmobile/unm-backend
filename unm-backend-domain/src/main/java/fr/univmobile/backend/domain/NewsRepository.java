package fr.univmobile.backend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsRepository extends JpaRepository<News, Long> {

	News findByLinkAndId(String link, String id);

	@Query("Select n from News n where n.feed.university = :universityId or n.feed.university = null order by n.createdOn desc")
	Page<News> findNewsForUniversity(@Param("universityId") University universityId, Pageable pageable);
}