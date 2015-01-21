package fr.univmobile.backend.domain;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment extends AuditableEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	private String title;
	@Column(nullable = false)
	private String message;
	@Column(nullable = false)
	private boolean active = true;
	@ManyToOne
	private Poi poi;

	@Override
	public String toString() {
		return String.format("Comment[id='%s', title='%s']", id, title);
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Poi getPoi() {
		return poi;
	}

	public void setPoi(Poi poi) {
		this.poi = poi;
	}

	public String getAuthor() {
		return this.getCreatedOn() == null ? null : String.format("%s", this.getCreatedBy().getDisplayName());
	}
}
