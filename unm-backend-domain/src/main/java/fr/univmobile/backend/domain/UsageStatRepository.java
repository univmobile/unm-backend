package fr.univmobile.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageStatRepository extends JpaRepository<UsageStat, Long> {
}
