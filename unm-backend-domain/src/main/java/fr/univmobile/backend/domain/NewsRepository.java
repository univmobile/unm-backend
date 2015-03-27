package fr.univmobile.backend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsRepository extends JpaRepository<News, Long> {

	News findByLinkAndId(String link, String id);

	/** We add in the news the news of the related CROUS */
	@Query("Select n from News n where n.feed.active  = TRUE and n.feed.university = :universityId or n.feed.university in (select uc.crous from UniversityCrous uc where uc.university.id = :universityId) or n.feed.university = null order by n.publishedDate desc")
	Page<News> findNewsForUniversity(@Param("universityId") University universityId, Pageable pageable);

	News findByLinkAndTitle(String urlString, String title);

	News findByLinkAndRestoId(String urlString, String attribute);

	News findByGuidAndFeed(String guid, Feed feed);

}