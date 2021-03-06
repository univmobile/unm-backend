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

	@XPath("atom:content/atom:description")
	@Nullable
	String getDescription();

	/**
	 * e.g. 1 (poiTypeLabel = "Université")
	 */
	@XPath("atom:content/atom:poiType/@id")
	int[] getPoiTypeIds();

	/**
	 * e.g. "Université"
	 */
	@XPath("atom:content/atom:poiType/@label")
	String[] getPoiTypeLabels();

	/**
	 * e.g. 1 (poiCategoryLabel = "Plans")
	 */
	@XPath("atom:content/atom:poiType/@categoryId")
	int[] getPoiCategoryIds();

	boolean isNullPoiCategoryIds();

	/**
	 * e.g. "Plans"
	 */
	@XPath("atom:content/atom:poiType/@categoryLabel")
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

	@XPath("atom:content/@active = 'true'")
	boolean isActive();

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

	@XPath("atom:content/atom:university/@id")
	String[] getUniversities();

	int sizeOfUniversities();

	boolean isNullUniversities();

	@XPath("atom:content/atom:address/atom:url")
	String[] getUrls();

	@XPath("atom:content/atom:address/atom:phone")
	String[] getPhones();

	@XPath("atom:content/atom:address/atom:fax")
	String[] getFaxes();

	@XPath("atom:content/atom:address/atom:email")
	String[] getEmails();

	@XPath("atom:content/atom:address")
	Address[] getAddresses();

	interface Address {

		@XPath("atom:floor")
		String getFloor();

		@XPath("atom:openingHours")
		String getOpeningHours();

		@XPath("atom:itinerary")
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

		@XPath("atom:fullAddress")
		@Nullable
		String getFullAddress();
	}

	@XPath("atom:content/@parentUid")
	int getParentUid();

	boolean isNullParentUid();

	@XPath("atom:content/atom:child[@active = 'true']/@uid")
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

	@XPath("atom:content/atom:attachment")
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
