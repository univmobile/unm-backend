package fr.univmobile.backend.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsageStatRepository extends JpaRepository<UsageStat, Long> {

	@Query("Select new fr.univmobile.backend.domain.UsageStatDto(s.source, count(s.source)) from UsageStat s where s.university = ?1 and s.createdOn between ?2 and ?3 group by s.source")
	List<UsageStatDto> getUsageStatsByUniversity(University university, Date from, Date to);
	
}
