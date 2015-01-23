package fr.univmobile.backend.jobs.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import fr.univmobile.backend.domain.Poi;

@Entity
public class RestoMenu {

	@Id
	@GeneratedValue
	private Long id;

	private Date effectiveDate;
	@Lob
	private String description;

	@ManyToOne
	@JoinColumn(name = "poi", nullable = false)
	private Poi poi;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Poi getPoi() {
		return poi;
	}

	public void setPoi(Poi poi) {
		this.poi = poi;
	}

}
