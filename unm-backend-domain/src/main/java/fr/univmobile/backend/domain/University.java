package fr.univmobile.backend.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "university")
public class University extends AuditableEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true, nullable = false)
	private String title;
	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonIgnore
	private Region region;

	@Override
	public String toString() {
		return String.format("University[id='%s', title='%s']", id, title);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

}
