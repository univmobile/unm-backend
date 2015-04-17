package fr.univmobile.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface TokenRepository extends JpaRepository<Token, Long> {
    public Token findByToken(String token);
}
