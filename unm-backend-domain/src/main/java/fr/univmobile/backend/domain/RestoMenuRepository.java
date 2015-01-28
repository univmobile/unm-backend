package fr.univmobile.backend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface RestoMenuRepository extends JpaRepository<RestoMenu, Long> {

    @Query("Select r from RestoMenu r where r.effectiveDate >= CURRENT_DATE and r.poi = :poiId order by r.effectiveDate asc ")
    Page<RestoMenu> findRestoMenuForPoi(@Param("poiId") Poi poiId, Pageable pageable);

    @RestResource(exported = false)
    @Query("Select COUNT(*) from RestoMenu r where r.effectiveDate >= CURRENT_DATE and r.poi = :poi order by r.effectiveDate asc ")
    Long CountRestoMenuesForPoi(@Param("poi") Poi poi);
}