package fr.univmobile.backend.domain;

import java.util.Collection;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
	Collection<Comment> findByPoi(Poi poi);
}
