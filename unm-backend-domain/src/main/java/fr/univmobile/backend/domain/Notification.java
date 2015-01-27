package fr.univmobile.backend.domain;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "notification")
@EntityListeners({ AuditingEntityListener.class })
@NamedQueries({
		@NamedQuery(name="Notification.searchByCreatedDate", query="Select n from Notification n where n.createdOn>=:date order by n.createdOn Desc"),
		@NamedQuery(name="Notification.countByCreatedDate", query="Select COUNT(*) from Notification n where n.createdOn>=:date")
})
public class Notification extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Lob
	@Column(nullable = false)
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
}