package fr.univmobile.commons.datasource;

import javax.annotation.Nullable;

import fr.univmobile.commons.datasource.Entry;
import net.avcompris.binding.annotation.XPath;

public interface MyUser extends Entry<MyUser> {

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

	void setDisplayName(String displayName);

	/**
	 * e.g. "M."
	 */
	@XPath("atom:content/atom:supannCivilite")
	@Nullable
	String getSupannCivilite();

	void setSupannCivilite(String supannCivilite);

	/**
	 * e.g. "dandriana@univ-paris1.fr"
	 */
	@XPath("atom:content/atom:remoteUser")
	String getRemoteUser();

	void setRemoteUser(String remoteUser);

	/**
	 * e.g. "David.Andriana@univ-paris1.fr"
	 */
	@XPath("atom:content/atom:mail")
	String getMail();

	void setMail(String mail);

	@XPath("concat(atom:category/@term, ':', atom:content/atom:uid)")
	@Override
	String toString();
}
