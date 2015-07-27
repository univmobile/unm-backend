package fr.univmobile.backend.domain;

import java.util.Set;

import javax.persistence.*;

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
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private University university;

	// The pois of an imageMap are used only in that imageMap, they should be removed at the same time than the imageMap
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "imageMap", orphanRemoval = true, cascade = CascadeType.REMOVE)
	@OrderBy("id ASC")
	private Set<Poi> pois;

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

	public Set<Poi> getPois() {
		return pois;
	}

	public University getUniversity() {
		return university;
	}

	public void setUniversity(University university) {
		this.university = university;
	}
	
}