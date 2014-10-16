package fr.univmobile.backend.core;

import javax.annotation.Nullable;

import net.avcompris.binding.annotation.Namespaces;
import net.avcompris.binding.annotation.XPath;

@Namespaces("xmlns:atom=http://www.w3.org/2005/Atom")
@XPath("self::university")
public interface University {

	/**
	 * e.g. "paris13"
	 */
	@XPath("id")
	String getId();

	/**
	 * e.g. "Paris Nord"
	 */
	@XPath("title")
	String getTitle();

	@XPath("shibboleth/@identityProvider")
	@Nullable
	String getShibbolethIdentityProvider();
}
