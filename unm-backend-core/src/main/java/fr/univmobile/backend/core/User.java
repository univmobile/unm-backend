package fr.univmobile.backend.core;

import javax.annotation.Nullable;

import fr.univmobile.commons.datasource.Entry;
import net.avcompris.binding.annotation.XPath;

public interface User extends Entry<User> {

	/**
	 * e.g. "dandriana"
	 */
	@XPath("atom:content/atom:uid")
	String getUid();

	/**
	 * e.g. "David Andriana"
	 */
	@XPath("atom:content/atom:displayName")
	String getDisplayName();

	/**
	 * e.g. "M."
	 */
	@XPath("atom:content/atom:supannCivilite")
	@Nullable
	String getSupannCivilite();

	/**
	 * e.g. "dandriana@univ-paris1.fr"
	 */
	@XPath("atom:content/atom:remoteUser")
	String getRemoteUser();
	
	/**
	 * e.g. "David.Andriana@univ-paris1.fr"
	 */
	@XPath("atom:content/atom:mail")
	String getMail();

	boolean isNullMail();
	
	@XPath("concat(atom:category/@term, ':', atom:content/atom:uid)")
	@Override
	String toString();
	
	@XPath("atom:content/@profileImageUrl")
	@Nullable
	String getProfileImageUrl();
	
	@XPath("atom:content/atom:description")
	@Nullable
	String getDescription();
	
	@XPath("atom:content/atom:login_classic[@active = 'true']/atom:password")
	// @Nullable // Because it will be stored as detached in the J2EE session
	Password getPassword();
	
	boolean isNullPassword();
	
	interface Password {
		
		@XPath("@saltPrefix")
		String getSaltPrefix();
		
		@XPath("@encryptionAlgorithm")
		String getEncryptionAlgorithm();
		
		@XPath("@encrypted")
		String getEncrypted();
	}
	
	@XPath("atom:content/atom:primaryUser")
	RefUser getPrimaryUser();
	
	boolean isNullPrimaryUser();
	
	@XPath("atom:content/atom:secondaryUser")
	RefUser[] getSecondaryUsers();
	
	int sizeOfSecondaryUsers();
	
	interface RefUser {
		
		@XPath("atom:uid")
		String getUid();
		
		@XPath("atom:remoteUser")
		String getRemoteUser();
	}
	
	@XPath("atom:content/atom:twitter/@screenName")
	String getTwitterScreenName();
	
	boolean isNullTwitterScreenName();
}
