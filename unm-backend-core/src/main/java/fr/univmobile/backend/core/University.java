package fr.univmobile.backend.core;

import javax.annotation.Nullable;

import net.avcompris.binding.annotation.Namespaces;
import net.avcompris.binding.annotation.XPath;

@Namespaces("xmlns:atom=http://www.w3.org/2005/Atom")
public interface University {

	/**
	 * e.g. "paris13"
	 */
	@XPath("self::university/id")
	String getId();

	/**
	 * e.g. "Paris Nord"
	 */
	@XPath("self::university/title")
	String getTitle();

	@XPath("self::university/shibboleth/@identityProvider")
	@Nullable
	String getShibbolethIdentityProvider();
}
