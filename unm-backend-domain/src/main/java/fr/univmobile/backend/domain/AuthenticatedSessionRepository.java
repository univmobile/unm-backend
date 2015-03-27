package fr.univmobile.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = false)
public interface AuthenticatedSessionRepository extends JpaRepository<AuthenticatedSession, Long> {

	public AuthenticatedSession findByToken(String token);
}