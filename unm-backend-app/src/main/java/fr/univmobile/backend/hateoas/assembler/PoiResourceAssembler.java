package fr.univmobile.backend.hateoas.assembler;

import fr.univmobile.backend.PoiController;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.hateoas.resource.PoiResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class PoiResourceAssembler extends ResourceAssemblerSupport<Poi, PoiResource> {

    private static final String POIS_PATH = "api/pois/";
    private static final String IMAGE_MAP_PATH = "/imageMap";
    private static final String UPDATED_BY_PATH = "/updatedBy";
    private static final String CATEGORY_PATH = "/category";
    private static final String PARENT_PATH = "/parent";
    private static final String UNIVERSITY_PATH = "/university";
    private static final String CHILDREN_PATH = "/children";
    private static final String CREATED_BY_PATH = "/createdBy";

    @Value("${baseURL}")
    private String baseUrl;

    public PoiResourceAssembler() {
        super(PoiController.class, PoiResource.class);
    }

    @Override
    public PoiResource toResource(Poi poi) {
        PoiResource poiResource = null;
        if (poi != null) {
            poiResource = new PoiResource();

            poiResource.name = poi.getName();
            poiResource.description = poi.getDescription();
            poiResource.lat = poi.getLat();
            poiResource.lng = poi.getLng();
            poiResource.markerType = poi.getMarkerType();
            poiResource.active = poi.isActive();
            poiResource.logo = poi.getLogo();
            poiResource.address = poi.getAddress();
            poiResource.floor = poi.getFloor();
            poiResource.zipcode = poi.getZipcode();
            poiResource.city = poi.getCity();
            poiResource.country = poi.getCountry();
            poiResource.itinerary = poi.getItinerary();
            poiResource.openingHours = poi.getOpeningHours();
            poiResource.phones = poi.getPhones();
            poiResource.email = poi.getEmail();
            poiResource.url = poi.getUrl();
            poiResource.attachmentId = poi.getAttachmentId();
            poiResource.attachmentType = poi.getAttachmentType();
            poiResource.attachmentTitle = poi.getAttachmentTitle();
            poiResource.attachmentUrl = poi.getAttachmentUrl();
            poiResource.qrCode = poi.getQrCode();
            poiResource.legacyIds = poi.getLegacyIds();

            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId()));
            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId() + IMAGE_MAP_PATH, "imageMap"));
            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId() + UPDATED_BY_PATH, "updatedBy"));
            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId() + CATEGORY_PATH, "category"));
            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId() + PARENT_PATH, "parent"));
            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId() + UNIVERSITY_PATH, "university"));
            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId() + CHILDREN_PATH, "children"));
            poiResource.add(new Link(baseUrl + POIS_PATH + poi.getId() + CREATED_BY_PATH, "createdBy"));
        }
        return poiResource;
    }
}