package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {

	List<Comment> findByPoi(Poi poi);
	
	List<Comment> findTop80ByOrderByCreatedOnDesc();

}
