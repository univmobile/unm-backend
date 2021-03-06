package fr.univmobile.commons.datasource;

import java.io.Serializable;

import javax.xml.soap.Node;

import net.avcompris.binding.annotation.Namespaces;
import net.avcompris.binding.annotation.XPath;

import org.joda.time.DateTime;

@Namespaces("xmlns:atom=http://www.w3.org/2005/Atom")
@XPath("/atom:entry")
public interface Entry<T extends Entry<?>> extends Serializable {

	@XPath("atom:id")
	String getId();

	@XPath("atom:link[@rel = 'parent']/@href")
	String getParentId();

	@XPath("atom:link[@rel = 'parent']")
	boolean isNullParent();

	@XPath("atom:updated")
	DateTime getUpdated();

	/**
	 * e.g. "dandriana"
	 */
	@XPath("atom:title")
	String getTitle();

	/**
	 * e.g. "users"
	 */
	@XPath("atom:category/@term")
	String getCategory();

	/**
	 * e.g. "dandriana"
	 */
	@XPath("atom:author/atom:name")
	String getAuthorName();

	@XPath("atom:author/atom:uri")
	String getAuthorURI();

	@XPath("concat(atom:category/@term, ':', atom:title)")
	@Override
	String toString();
	
	@XPath("atom:link[@rel = 'local']/@href")
	String getLocalRevfile();

	Node node();
}
