package fr.univmobile.backend.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "imagemap")
public class ImageMap extends AuditableEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	@Column(nullable = false)
	private String url;
	private String description;
	@Column(nullable = false)
	private boolean active = true;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "imageMap")
	private List<Poi> pois;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return String.format("ImageMap[id='%s', name='%s']", id, name);
	}

	public List<Poi> getPois() {
		return pois;
	}
	
}