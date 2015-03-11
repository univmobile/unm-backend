package fr.univmobile.backend.domain;

import javax.persistence.*;

@Entity
@Table(
        name = "bookmark",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "poi_id"})
)
public class Bookmark extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
