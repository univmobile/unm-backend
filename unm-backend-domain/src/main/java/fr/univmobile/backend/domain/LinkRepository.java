package fr.univmobile.backend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Page<Link> findByUniversity(@Param("universityId")University universityId);
}
