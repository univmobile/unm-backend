package fr.univmobile.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {

	News findByLinkAndId(String link, String id);

}
