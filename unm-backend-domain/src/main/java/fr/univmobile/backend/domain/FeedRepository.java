package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Feed, Long> {

	Page<Feed> findByUniversityOrderByCreatedOnDesc(@Param("universityId")University universityId, Pageable pageable);
	
	@Query("Select f from Feed f where f.active = TRUE and (f.university = :universityId or f.university in (select uc.crous from UniversityCrous uc where uc.university.id = :universityId) or f.university = null) AND f.type = 'RSS' order by f.name asc")
	Page<Feed> findAllActiveRssFeedsForUniversity(@Param("universityId")University universityId, Pageable pageable);
	
	List<Feed> findByName(String string);
	
	List<Feed> findByActiveIsTrueAndType(Feed.Type type);

}