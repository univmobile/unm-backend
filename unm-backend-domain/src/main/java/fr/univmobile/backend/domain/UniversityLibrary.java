package fr.univmobile.backend.domain;

import javax.persistence.*;

@Entity
@Table(
		name = "university_library",
		uniqueConstraints = @UniqueConstraint( columnNames = { "poi_id", "university_id" } )
)
public class UniversityLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;
    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Poi getPoi() {
        return poi;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

	public Long getPoiId() {
		return this.poi.getId();
	}
	
	public String getPoiName() {
		if (poi != null) {
			return poi.getName();
		} else {
			return null;
		}
	}

	public Long getUniversityId() {
		return this.university.getId();
	}


}
