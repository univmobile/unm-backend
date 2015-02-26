package fr.univmobile.backend.domain;

import javax.persistence.*;

@Entity
@Table(name = "bookmark")
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
    
    public Long getPoiId() {
    	if (poi != null) {
    		return poi.getId();
    	} else {
    		return null;
    	}
    }
    
    public String getPoiName() {
    	if (poi != null) {
    		return poi.getName();
    	} else {
    		return null;
    	}
    }
    
    public Long getPoiCategoryId() {
    	if (poi != null) {
    		return poi.getCategoryId();
    	} else {
    		return null;
    	}
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
