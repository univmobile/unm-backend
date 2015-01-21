package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "menu", path = "menues")
public interface MenuRepository extends JpaRepository<Menu, Long> {

	@Override
	@Query("From Menu m order by m.grouping,m.ordinal")
	public List<Menu> findAll();

}
