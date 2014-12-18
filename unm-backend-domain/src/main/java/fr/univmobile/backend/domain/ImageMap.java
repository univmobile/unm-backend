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
	
	/*
	@OneToMany(mappedBy="region")
	private Collection<University> universities = new ArrayList<University>();
	*/

	@Override
	public String toString() {
		return String.format("ImageMap[id='%s', name='%s']", id, name);
	}
}
