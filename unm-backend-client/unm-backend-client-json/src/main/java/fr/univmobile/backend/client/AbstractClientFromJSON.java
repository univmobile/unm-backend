package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import net.avcompris.binding.json.JsonBinder;
import net.avcompris.binding.json.impl.DomJsonBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;

abstract class AbstractClientFromJSON<E> {

	@Inject
	public AbstractClientFromJSON(final E jsonClient) {

		this.jsonClient = checkNotNull(jsonClient, "jsonClient");
	}

	protected final E jsonClient;

	private static Log log = LogFactory.getLog(AbstractClientFromJSON.class);

	private static final JsonBinder binder = new DomJsonBinder();

	protected static final <U> U unmarshall(final String json, final Class<U> clazz) {

		if (log.isDebugEnabled()) {
			log.debug("json.length(): " + json.length());
			log.debug("json: "
					+ (json.length() <= 80 ? json
							: (json.substring(0, 80) + "...")));
		}

		final Object jsonObject = JSONValue.parse(json);

		final U value = binder.bind(jsonObject, clazz);

		return value;
	}
}
