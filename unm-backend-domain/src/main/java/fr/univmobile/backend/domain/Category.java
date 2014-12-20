package fr.univmobile.backend.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "category")
public class Category extends AuditableEntityWithLegacy {
	
	public enum Type {
		PLANS(1), BON_PLANS(2), IMAGE_MAPS(3);
		public final int type;
		private Type(int type) {
			this.type = type;
		}
	}
	
	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	private String description;
	@Column(nullable = false)
	private boolean active = true;
	private String legacy;
	@ManyToOne
	@JsonIgnore
	private Category parent;
	@OneToMany(mappedBy="parent")
	@JsonIgnore
    private Collection<Category> children = new ArrayList<Category>();

	@OneToMany(mappedBy="category")
	@JsonIgnore
    private Collection<Poi> pois = new ArrayList<Poi>();

	@Override
	public String toString() {
		return String.format("Category[id=%d, name='%s']", id, name);
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLegacy() {
		return legacy;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Collection<Category> getChildren() {
		return children;
	}

	public Collection<Poi> getPois() {
		return pois;
	}
	
	public static String getPlansLegacy() {
		return buildRootLegacy((long) Type.PLANS.type);
	}

	public static String getBonPlansLegacy() {
		return buildRootLegacy((long) Type.BON_PLANS.type);
	}

	public static String getImageMapsLegacy() {
		return buildRootLegacy((long) Type.IMAGE_MAPS.type);
	}
}
