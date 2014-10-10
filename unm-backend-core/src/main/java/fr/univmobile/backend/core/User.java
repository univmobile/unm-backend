package fr.univmobile.backend.core;

import javax.annotation.Nullable;

import fr.univmobile.commons.datasource.Entry;
import net.avcompris.binding.annotation.XPath;

public interface User extends Entry<User> {

	/**
	 * e.g. "dandriana"
	 */
	@XPath("atom:content/uid")
	String getUid();

	/**
	 * e.g. "David Andriana"
	 */
	@XPath("atom:content/displayName")
	String getDisplayName();

	/**
	 * e.g. "M."
	 */
	@XPath("atom:content/supannCivilite")
	@Nullable
	String getSupannCivilite();

	/**
	 * e.g. "dandriana@univ-paris1.fr"
	 */
	@XPath("atom:content/remoteUser")
	String getRemoteUser();
	
	/**
	 * e.g. "David.Andriana@univ-paris1.fr"
	 */
	@XPath("atom:content/mail")
	String getMail();

	boolean isNullMail();
	
	@XPath("concat(atom:category/@term, ':', atom:content/uid)")
	@Override
	String toString();
	
	@XPath("atom:content/@profileImageUrl")
	@Nullable
	String getProfileImageUrl();
	
	@XPath("atom:content/description")
	@Nullable
	String getDescription();

	@XPath("atom:content/login_classic/@active")
	boolean getPasswordEnabled();

	@XPath("atom:content/login_classic[@active = 'true']/password")
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
	
	@XPath("atom:content/primaryUser")
	RefUser getPrimaryUser();
	
	boolean isNullPrimaryUser();
	
	@XPath("atom:content/secondaryUser")
	RefUser[] getSecondaryUsers();
	
	int sizeOfSecondaryUsers();
	
	interface RefUser {
		
		@XPath("uid")
		String getUid();
		
		@XPath("remoteUser")
		String getRemoteUser();
	}
	
	@XPath("atom:content/twitter/@screenName")
	String getTwitterScreenName();
	
	boolean isNullTwitterScreenName();
}
