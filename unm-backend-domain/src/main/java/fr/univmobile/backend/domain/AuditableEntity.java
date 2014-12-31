package fr.univmobile.backend.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class AuditableEntity {
	
	@CreatedBy
	@ManyToOne(fetch = FetchType.EAGER) // FIXME: Come back to lazy after fixing comments.jsp
	@JoinColumn(name = "createdby")
	@JsonIgnore
	private User createdBy;
	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updatedby")
	@JsonIgnore
	private User updatedBy;
	@CreatedDate
	@Column(name = "createdon", nullable = false)
	@JsonIgnore
	private Date createdOn;
	@LastModifiedDate
	@Column(name = "updatedon", nullable = false)
	@JsonIgnore
	private Date updatedOn;

	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	public User getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
}
