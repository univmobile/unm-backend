package fr.univmobile.backend.domain;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "feed")
@EntityListeners({ AuditingEntityListener.class })
public class Feed extends AuditableEntity {

	public enum Type {
		RSS, RESTO;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(length = 512)
	String url;

	@Column
	@Enumerated(EnumType.STRING)
	Type type;

	@ManyToOne
	@JoinColumn(name = "university", nullable = true)
	private University university_id;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Type getType() {
		return type;
	}

	public University getUniversity_id() {
		return university_id;
	}

	public void setUniversity_id(University university_id) {
		this.university_id = university_id;
	}
	
}
