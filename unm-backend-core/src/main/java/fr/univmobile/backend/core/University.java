package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.Namespaces;
import net.avcompris.binding.annotation.XPath;

@Namespaces("xmlns:atom=http://www.w3.org/2005/Atom")
public interface University {

	/**
	 * e.g. "paris13"
	 */
	@XPath("self::atom:university/atom:id")
	String getId();

	/**
	 * e.g. "Paris Nord"
	 */
	@XPath("self::atom:university/atom:title")
	String getTitle();
}
