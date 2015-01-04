package fr.univmobile.backend.domain;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "poi")
public class Poi extends AuditableEntityWithLegacy {

	@TableGenerator(
			name = "poi_generator", 
			table = "jpa_sequence", 
			pkColumnName = "seq_name", 
			valueColumnName = "value", 
			allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "poi_generator")
	private Long id;
	@Column(nullable = false)
	private String name;
	private String description;
	private Double lat;
	private Double lng;
	@Column(name = "markertype")
	private String markerType;
	@Column(nullable = false)
	private boolean active = true;
	private String logo;
	private String address;
	private String floor;
	private String zipcode;
	private String city;
	private String country;
	private String itinerary;
	@Column(name = "openinghours")
	private String openingHours;
	private String phones;
	private String email;
	private String url;

	@Column(name = "attachmentid")
	private Integer attachmentId;
	@Column(name = "attachmenttype")
	private String attachmentType;
	@Column(name = "attachmenttitle")
	private String attachmentTitle;
	@Column(name = "attachmenturl")
	private String attachmentUrl;

	@ManyToOne
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private Poi parent;
	@OneToMany(mappedBy = "parent")
	@JsonIgnore
	private Collection<Poi> children;
	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private Category category;

	@ManyToOne
	@JoinColumn(nullable = false)
	private University university;

	@ManyToOne
	@JoinColumn(name = "imagemap_id")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private ImageMap imageMap;

	@Column(name = "qrcode")
	private String qrCode;
	
	@Override
	public String toString() {
		return String.format("Poi[id=%d, name='%s']", id, name);
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

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String getMarkerType() {
		return markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getItinerary() {
		return itinerary;
	}

	public void setItinerary(String itinerary) {
		this.itinerary = itinerary;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(String openingHours) {
		this.openingHours = openingHours;
	}

	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Integer attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getAttachmentTitle() {
		return attachmentTitle;
	}

	public void setAttachmentTitle(String attachmentTitle) {
		this.attachmentTitle = attachmentTitle;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public Poi getParent() {
		return parent;
	}

	public void setParent(Poi parent) {
		this.parent = parent;
	}

	public Collection<Poi> getChildren() {
		return children;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public University getUniversity() {
		return university;
	}

	public void setUniversity(University university) {
		this.university = university;
	}

	public ImageMap getImageMap() {
		return imageMap;
	}

	public void setImageMap(ImageMap imageMap) {
		this.imageMap = imageMap;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	// NEAREST POI

	public boolean isNear(double lat, double lng, Double metersAway) {
		return this.lat != null && this.lng != null && (getDistance(lat, lng) * 1000 <= metersAway);
	}

	public double getDistance(double lat, double lng) {
		double r = 6371; // Radius of the earth in km
		double dLat = deg2rad(this.lat - lat);
		double dLon = deg2rad(this.lng - lng);
		double a = (double) (Math.sin(dLat / 2) * Math.sin(dLat / 2) //
		+ Math.cos(deg2rad(this.lat)) //
				* Math.cos(deg2rad(lat)) //
				* Math.sin(dLon / 2) * Math.sin(dLon / 2));
		double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
		double d = r * c; // Distance in km
		return d;
	}

	private static double deg2rad(double deg) {
		return (double) (deg * (Math.PI / 180));
	}

}
