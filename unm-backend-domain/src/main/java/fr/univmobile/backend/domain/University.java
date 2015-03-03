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
	
}
