package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPoi(Poi poi);
	
	List<Comment> findTop80ByOrderByCreatedOnDesc();

	List<Comment> findTop10ByPoiOrderByIdDesc(Poi poi);

	@Query("Select c from Comment c where c.title like CONCAT('%',:val,'%') or c.message like CONCAT('%',:val,'%') order by c.createdOn desc")
	Page<Comment> searchValue(@Param("val") String val, Pageable pageable);
}
