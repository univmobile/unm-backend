package fr.univmobile.backend.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		name = "university_crous",
		uniqueConstraints = @UniqueConstraint( columnNames = { "crous_id", "university_id" } )
)
public class UniversityCrous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "crous_id", nullable = false)
    private University crous;
    
    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public University getCrous() {
		return crous;
	}
	
	public Long getCrousId() {
		if (crous != null) {
			return crous.getId();
		}
		return null;
	}

	public void setCrous(University crous) {
		this.crous = crous;
	}

	public University getUniversity() {
		return university;
	}
	
	public Long getUniversityId() {
		if (university != null) {
			return university.getId();
		}
		return null;
	}

	public void setUniversity(University university) {
		this.university = university;
	}

}
