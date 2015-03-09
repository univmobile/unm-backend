package fr.univmobile.backend.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "notification")
@EntityListeners({ AuditingEntityListener.class })
@NamedQueries({
		@NamedQuery(name="Notification.searchByCreatedDate", query="Select n from Notification n where n.createdOn>:date order by n.createdOn Desc"),
		@NamedQuery(name="Notification.countByCreatedDate", query="Select COUNT(n.id) from Notification n where n.createdOn>:date")
})
public class Notification extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	String content;
	
	@ManyToOne
	@JoinColumn(name = "university_id", nullable = true)
	private University university;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public University getUniversity() {
		return university;
	}

	public void setUniversity(University university) {
		this.university = university;
	}

	public Long getUniversityId(){
		Long universityId = null;
		if (university != null){
			universityId = university.getId();
		}
		return universityId;
	}
	
	public Date getNotificationTime() {
		return getCreatedOn();
	}
}