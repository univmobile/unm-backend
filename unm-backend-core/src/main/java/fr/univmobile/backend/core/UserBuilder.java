package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.XPath;
import fr.univmobile.commons.datasource.EntryBuilder;

/**
 * Setter methods that cannot exist in the {@link User} interface (when data is
 * already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface UserBuilder extends EntryBuilder<User>, User {

	UserBuilder setUid(String uid);

	UserBuilder setDescription(String description);

	UserBuilder setDisplayName(String displayName);

	UserBuilder setSupannCivilite(String supannCivilite);

	UserBuilder setRemoteUser(String remoteUser);

	UserBuilder setMail(String mail);
	
	UserBuilder setPasswordEnabled(boolean passwordEnabeld);

	@XPath("atom:content/atom:login_classic/atom:password/@saltPrefix")
	UserBuilder setPasswordSaltPrefix(String saltPrefix);
	
	@XPath("atom:content/atom:login_classic/atom:password/@encryptionAlgorithm")
	UserBuilder setPasswordEncryptionAlgorithm(String encryptionAlgorithm);
	
	@XPath("atom:content/atom:login_classic/atom:password/@encrypted")
	UserBuilder setPasswordEncrypted(String encrypted);
	
	UserBuilder setTwitterScreenName(String twitterScreenName);
}
