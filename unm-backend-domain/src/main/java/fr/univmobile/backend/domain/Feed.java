package fr.univmobile.backend.domain;

import java.util.Set;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "feed")
@EntityListeners({ AuditingEntityListener.class })
public class Feed extends AuditableEntity {

	public static enum Type {
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
	@JoinColumn(name = "university_id", nullable = true)
	private University university;

	@Column(nullable = false)
	private boolean active = true;

	@OneToMany(mappedBy = "feed", orphanRemoval = true)
	private Set<News> news;
	
	
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

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<News> getNews() {
		return news;
	}

	public void setNews(Set<News> news) {
		this.news = news;
	}
	
}
