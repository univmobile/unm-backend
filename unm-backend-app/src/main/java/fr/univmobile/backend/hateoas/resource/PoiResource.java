package fr.univmobile.backend.hateoas.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class PoiResource extends ResourceSupport {
    public String name;
    public String description;
    public Double lat;
    public Double lng;
    public String markerType;
    public Boolean active;
    public String logo;
    public String address;
    public String floor;
    public String zipcode;
    public String city;
    public String country;
    public String itinerary;
    public String openingHours;
    public String phones;
    public String email;
    public String url;
    public Integer attachmentId;
    public String attachmentType;
    public String attachmentTitle;
    public String attachmentUrl;
    public String qrCode;
    public List<Long> legacyIds;
}