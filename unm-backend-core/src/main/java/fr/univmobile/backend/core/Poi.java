package fr.univmobile.backend.core;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.avcompris.binding.annotation.XPath;

import org.joda.time.DateTime;

import fr.univmobile.commons.datasource.Entry;

public interface Poi extends Entry<Poi> {

	/**
	 * e.g. 1
	 */
	@XPath("atom:content/@uid")
	int getUid();

	/**
	 * e.g. "Université Panthéon-Sorbonne - Paris I"
	 */
	@XPath("atom:content/@name")
	String getName();

	@XPath("atom:content/description")
	@Nullable
	String getDescription();

	/**
	 * e.g. 1 (poiTypeLabel = "Université")
	 */
	@XPath("atom:content/poiType/@id")
	int[] getPoiTypeIds();

	/**
	 * e.g. "Université"
	 */
	@XPath("atom:content/poiType/@label")
	String[] getPoiTypeLabels();

	/**
	 * e.g. 1 (poiCategoryLabel = "Plans")
	 */
	@XPath("atom:content/poiType/@categoryId")
	int[] getPoiCategoryIds();

	boolean isNullPoiCategoryIds();

	/**
	 * e.g. "Plans"
	 */
	@XPath("atom:content/poiType/@categoryLabel")
	String[] getPoiCategoryLabels();

	/**
	 * e.g. "/images/universities/logos/univ_paris1.jpg"
	 */
	@XPath("atom:content/@logo")
	String getLogo();

	/**
	 * e.g. "48.84650925911,2.3459243774"
	 */
	@XPath("atom:content/@coordinates")
	String getCoordinates();

	// Author: Mauricio
	@XPath("atom:content/@active")
	String getActive();
	
	@XPath("atom:content/@markerType")
	MarkerType getMarkerType();

	@Nullable
	@XPath("atom:content/@createdAt")
	DateTime getCreatedAt();

	@Nullable
	@XPath("atom:content/@updatedAt")
	DateTime getUpdatedAt();

	@Nullable
	@XPath("atom:content/@createdBy")
	String getCreatedBy();

	@Nullable
	@XPath("atom:content/@updatedBy")
	String getUpdatedBy();

	@XPath("not(not(atom:content/@deletedAt))")
	boolean isDeleted();

	@XPath("atom:content/university/@id")
	String[] getUniversities();

	int sizeOfUniversities();

	boolean isNullUniversities();

	// Autor: Mauricio (begin)

	@XPath("atom:content/address/url")
	String[] getUrls();

	@XPath("atom:content/address/floor")
	String[] getFloors();

	@XPath("atom:content/address/@latitude")
	String[] getLatitudes();

	@XPath("atom:content/address/@longitude")
	String[] getLongitudes();

	@XPath("atom:content/address/openingHours")
	String[] getOpeningHours();

	@XPath("atom:content/address/itinerary")
	String[] getItineraries();

	@XPath("atom:content/address/zipCode")
	String[] getZipCodes();

	@XPath("atom:content/address/city")
	String[] getCities();

	@XPath("atom:content/address/countryCode")
	String[] getCountryCodes();

	@XPath("atom:content/address/fullAddress")
	String[] getFullAddresses();

	// Author: Mauricio (end)

	@XPath("atom:content/address/phone")
	String[] getPhones();

	@XPath("atom:content/address/fax")
	String[] getFaxes();

	@XPath("atom:content/address/email")
	String[] getEmails();

	@XPath("atom:content/address")
	Address[] getAddresses();

	interface Address {

		@XPath("floor")
		String getFloor();

		@XPath("openingHours")
		String getOpeningHours();

		@XPath("itinerary")
		String getItinerary();

		@XPath("@zipCode")
		String getZipCode();

		@XPath("@city")
		String getCity();

		@XPath("@countryCode")
		String getCountryCode();

		@XPath("@latitude")
		String getLatitude();

		@XPath("@longitude")
		String getLongitude();

		// Author: Mauricio
		@XPath("@phone")
		String getPhone();

		@XPath("fullAddress")
		@Nullable
		String getFullAddress();
	}

	@XPath("atom:content/@parentUid")
	int getParentUid();

	boolean isNullParentUid();

	@XPath("atom:content/child[@active = 'true']/@uid")
	int[] getChildren();

	int sizeOfChildren();

	boolean isNullChildren();

	enum MarkerType {

		POINT("point"), POLYGON("polygon"), OVERLAY("overlay");

		private MarkerType(final String label) {

			this.label = checkNotNull(label, "label");
		}

		public final String label;

		@Override
		public String toString() {

			return label;
		}
	}

	@XPath("atom:content/attachment")
	Attachment[] getAttachments();

	int sizeOfAttachments();

	interface Attachment {

		@XPath("@id")
		int getId();

		@XPath("@title")
		String getTitle();

		@XPath("@type")
		AttachmentType getType();

		@XPath("@url")
		String getUrl();
	}

	enum AttachmentType {

		IMAGE("image");

		private AttachmentType(final String label) {

			this.label = checkNotNull(label, "label");
		}

		public final String label;

		@Override
		public String toString() {

			return label;
		}
	}
}
