package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPoi(Poi poi);
	
	List<Comment> findTop80ByOrderByCreatedOnDesc();

	List<Comment> findTop10ByPoiOrderByIdDesc(Poi poi);

	Page<Comment> findByPoiOrderByCreatedOnDesc(@Param("poiId")Poi poiId, Pageable pageable);
	
	Page<Comment> findByPoi_UniversityOrderByCreatedOnDesc(@Param("universityId")University universityId, Pageable pageable);

	@PreAuthorize(value="hasRole('superadmin') or (hasRole('admin') and principal.university.id == #universityId)")
	@Query("Select c from Comment c where c.poi.university.id = :universityId and (c.title like CONCAT('%',:val,'%') or c.message like CONCAT('%',:val,'%')) order by c.createdOn desc")
	Page<Comment> searchValue(@Param("val") String val, @Param("universityId") Long universityId, Pageable pageable);
	
	@PreAuthorize(value="hasRole('superadmin')")
	@Query("Select c from Comment c where (c.title like CONCAT('%',:val,'%') or c.message like CONCAT('%',:val,'%')) order by c.createdOn desc")
	Page<Poi> searchGlobalValue(@Param("val") String val, Pageable pageable);
	
}
