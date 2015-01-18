package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPoi(Poi poi);
	
	List<Comment> findTop80ByOrderByCreatedOnDesc();

	List<Comment> findTop10ByPoiOrderByIdDesc(Poi poi);

}
