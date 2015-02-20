package fr.univmobile.backend.domain;

import javax.persistence.*;

@Entity
@Table(name = "university_library")
public class UniversityLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;

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
}
