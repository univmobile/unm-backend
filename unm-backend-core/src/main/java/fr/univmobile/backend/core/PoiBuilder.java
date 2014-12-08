package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;

/**
 * Setter methods that cannot exist in the {@link Poi} interface (when data is
 * already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface PoiBuilder extends EntryBuilder<Poi>, Poi {

	PoiBuilder setUid(int uid);

	PoiBuilder setParentUid(int parentUid);

	PoiBuilder setName(String name);

	// Author: Mauricio (begin)
	
	PoiBuilder setCategoryId(int categoryUid);

	PoiBuilder setUniversityIds(String universities);

	PoiBuilder setFloors(String floor);

	PoiBuilder setOpeningHours(String openingHours);

	PoiBuilder setPhones(String phone);

	PoiBuilder setFullAddresses(String fullAddress);

	PoiBuilder setEmails(String email);

	PoiBuilder setItineraries(String itinerary);

	PoiBuilder setUrls(String url);

	PoiBuilder setCoordinates(String coordinates);
	
	PoiBuilder setActive(String active);

	// Some address specific attributes

	PoiBuilder setLatitudes(String latitude);

	PoiBuilder setLongitudes(String longitude);

	PoiBuilder setCities(String city);

	PoiBuilder setZipCodes(String zipCode);

	PoiBuilder setCountryCodes(String countryCode);

	// Author: Mauricio (end)
}
