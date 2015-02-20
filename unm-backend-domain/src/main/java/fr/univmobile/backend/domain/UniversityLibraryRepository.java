package fr.univmobile.backend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UniversityLibraryRepository extends JpaRepository<UniversityLibrary, Long> {
    Page<UniversityLibrary> findByUniversity(@Param("universityId")University universityId, Pageable pageable);
}
