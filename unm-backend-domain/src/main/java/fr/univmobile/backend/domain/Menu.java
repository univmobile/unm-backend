package fr.univmobile.backend.domain;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "menu")
@EntityListeners({ AuditingEntityListener.class })
public class Menu extends AuditableEntity {

	public enum Group {
		MS, AU, TT, MU;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean active = true;

	@Column(nullable = false)
	private int ordinal = 0;

	@Column(length = 512)
	String url;

	@Column(columnDefinition = "TEXT")
	String content;

	@Column
	@Enumerated(EnumType.STRING)
	Group grouping;

	@ManyToOne
	@JoinColumn(name = "university_id", nullable = true)
	private University university;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public Group getGrouping() {
		return grouping;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setGrouping(Group grouping) {
		this.grouping = grouping;
	}

}
