package fr.univmobile.backend.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "category")
public class Category extends AuditableEntityWithLegacy {
	
	public enum Type {
		PLANS(1), BON_PLANS(2), IMAGE_MAPS(3), LIBRARIES(4);
		public final long type;
		private Type(long type) {
			this.type = type;
		}
	}
	
	@TableGenerator(
			name = "category_generator", 
			table = "jpa_sequence", 
			pkColumnName = "seq_name", 
			valueColumnName = "value", 
			allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "category_generator")
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	private String description;
	@Column(nullable = false)
	private boolean active = true;
	@ManyToOne
	@JsonIgnore
	private Category parent;
	@OneToMany(mappedBy="parent")
	@JsonIgnore
    private Collection<Category> children = new ArrayList<Category>();

	@OneToMany(mappedBy="category")
	@JsonIgnore
    private Collection<Poi> pois = new ArrayList<Poi>();
	
	private Long apiParisId;

	@Column(name = "activeiconurl")
	private String activeIconUrl;
	@Column(name = "inactiveiconurl")
	private String inactiveIconUrl;
	@Column(name = "markerIconUrl")
	private String markerIconUrl;

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
		return buildRootLegacy(Type.PLANS.type);
	}

	public static String getBonPlansLegacy() {
		return buildRootLegacy(Type.BON_PLANS.type);
	}

	public static String getImageMapsLegacy() {
		return buildRootLegacy(Type.IMAGE_MAPS.type);
	}

	public static String getLibrariesLegacy() {
		return buildRootLegacy(Type.LIBRARIES.type);
	}
	
	public Long getApiParisId() {
		return apiParisId;
	}

	public void setApiParisId(Long apiParisId) {
		this.apiParisId = apiParisId;
	}

	public String getActiveIconUrl() {
		return activeIconUrl;
	}

	public void setActiveIconUrl(String activeIconUrl) {
		this.activeIconUrl = activeIconUrl;
	}

	public String getInactiveIconUrl() {
		return inactiveIconUrl;
	}

	public void setInactiveIconUrl(String inactiveIconUrl) {
		this.inactiveIconUrl = inactiveIconUrl;
	}

	public String getMarkerIconUrl() {
		return markerIconUrl;
	}

	public void setMarkerIconUrl(String markerIconUrl) {
		this.markerIconUrl = markerIconUrl;
	}
}
