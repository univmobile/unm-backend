package fr.univmobile.backend.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "region")
public class Region extends AuditableEntity {

	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	@Column(unique = true, nullable = false)
	private String label;
	@Column(unique = true, nullable = false)
	private String url;
	@OneToMany(mappedBy="region")
	private Collection<University> universities = new ArrayList<University>();

	@Override
	public String toString() {
		return String.format("Region[id='%s', name='%s']", id, name);
	}

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Collection<University> getUniversities() {
		return universities;
	}
}
