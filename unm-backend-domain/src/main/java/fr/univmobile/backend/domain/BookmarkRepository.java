package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	
	List<Bookmark> findByPoi(Poi poi);
	
}
