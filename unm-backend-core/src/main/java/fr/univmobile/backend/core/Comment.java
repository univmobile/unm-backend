package fr.univmobile.backend.core;

import static com.google.common.base.Preconditions.checkNotNull;
import net.avcompris.binding.annotation.XPath;

import org.joda.time.DateTime;

import fr.univmobile.commons.datasource.Entry;

public interface Comment extends Entry<Comment>, EntryRef {

	/**
	 * e.g. 1
	 */
	@XPath("atom:content/@uid")
	int getUid();

	/**
	 * equals: getUid()
	 */
	@XPath("atom:content/@uid")
	@Override
	String getEntryRefId();

	/**
	 * e.g. "Dominique"
	 */
	@XPath("atom:content/@postedBy")
	String getPostedBy();

	@XPath("atom:content/@postedAt")
	DateTime getPostedAt();

	@XPath("atom:content/atom:context")
	Context[] getContexts();

	@XPath("atom:content/atom:context[1]")
	Context getMainContext();

	@XPath("atom:content/atom:message")
	String getMessage();

	interface Context {

		@XPath("@type")
		ContextType getType();

		@XPath("@uid")
		int getUid();
	}

	enum ContextType {

		LOCAL_POI("local:poi");

		private ContextType(final String name) {

			this.name = checkNotNull(name, "name");
		}

		public final String name;

		@Override
		public String toString() {

			return name;
		}
	}

	@Override
	@XPath("concat('{comment:', atom:content/@uid, '}')")
	String toString();
}
