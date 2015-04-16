package fr.univmobile.backend.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	@ManyToOne(fetch = FetchType.EAGER) // FIXME: Come back to lazy
	@JoinColumn(nullable = false)
	@JsonIgnore
	private Region region;
	@Column(name = "moderatecomments", nullable = false)
	private boolean moderateComments = false;
	@Column(name = "logourl")
	private String logoUrl;
	@Column(name = "mobileshibbolethurl")
	private String mobileShibbolethUrl;
	
	/** Central location for the university, to center correctly the map if there is no geolocalisation */
	private Double centralLat;
	private Double centralLng;
	
	/** Return true if the university is showing a CROUS */
	@Column(name = "iscrous", nullable = false)
	private boolean crous;
	
	private boolean active;

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

	public boolean getModerateComments() {
		return this.moderateComments;
	}

	public void setModerateComments(boolean moderateComments) {
		this.moderateComments = moderateComments;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getMobileShibbolethUrl() {
		return mobileShibbolethUrl;
	}

	public void setMobileShibbolethUrl(String mobileShibbolethUrl) {
		this.mobileShibbolethUrl = mobileShibbolethUrl;
	}

	public boolean allowBonplans() {
		return this.region.getAllowBonplans();
	}
	
	public Long getRegionId() {
		return this.region.getId();
	}
	
	public String getRegionName() {
		return this.region.getName();
	}

	public Double getCentralLat() {
		return centralLat;
	}

	public void setCentralLat(Double centralLat) {
		this.centralLat = centralLat;
	}

	public Double getCentralLng() {
		return centralLng;
	}

	public void setCentralLng(Double centralLng) {
		this.centralLng = centralLng;
	}

	public boolean isCrous() {
		return crous;
	}

	public void setCrous(boolean crous) {
		this.crous = crous;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
