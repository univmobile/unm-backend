package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.XPath;

//@Namespaces("xmlns:atom=http://www.w3.org/2005/Atom")
//@XPath("/entry")
public interface User extends Entry {

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
	
	@XPath("concat(atom:category/@term, ':', atom:content:atom:uid)")
	@Override
	String toString();
}
