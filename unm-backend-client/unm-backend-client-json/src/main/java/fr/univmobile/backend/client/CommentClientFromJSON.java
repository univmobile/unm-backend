package fr.univmobile.backend.client;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import fr.univmobile.backend.client.json.CommentJSONClient;

public class CommentClientFromJSON extends
		AbstractClientFromJSON<CommentJSONClient> implements CommentClient {

	@Inject
	public CommentClientFromJSON(final CommentJSONClient jsonClient) {

		super(jsonClient);
	}

	private static Log log = LogFactory.getLog(CommentClientFromJSON.class);

	@Override
	public Comment[] getCommentsByPoiId(final int poiId) throws IOException,
			ClientException {

		if (log.isDebugEnabled()) {
			log.debug("getCommentsByPoiId():" + poiId + "...");
		}

		final String json;

		try {

			json = jsonClient.getCommentsJSONByPoiId(poiId);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}

		return comments(json);
	}

	@Override
	public Comment[] getMostRecentComments(final int limit) throws IOException,
			ClientException {

		if (log.isDebugEnabled()) {
			log.debug("getMostRecentComments():" + limit + "...");
		}

		final String json;

		try {

			json = jsonClient.getMostRecentCommentsJSON(limit);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}

		return comments(json);
	}

	@Override
	public Comment[] searchComments(final String query, final int limit)
			throws IOException, ClientException {

		if (log.isDebugEnabled()) {
			log.debug("searchComments(String, int)():" + query + ", " + limit
					+ "...");
		}

		final String json;

		try {

			json = jsonClient.searchCommentsJSON(query, limit);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}

		return comments(json);
	}

	private Comment[] comments(final String json) {

		return unmarshall(json, CommentsJSON.class).getComments();
	}

	@XPath("/*")
	public interface CommentsJSON {

		@XPath("comments")
		CommentJSON[] getComments();

		/*
		 * "id": "9120122", "url":
		 * "http://localhost:8380/unm-backend-mock/json/comments/dandriana/9120122"
		 * , "postedAt": "2014-08-15 09:34:45.894+02:00", "displayPostedAt":
		 * "15 août — 9 h 34", "source": "UnivMobile", "author":{ "username":
		 * "dandriana", "displayName": "David Andriana", "timeZone":
		 * "Central European Time Zone (UTC+01:00)", "lang": "fr",
		 * "profileImage": { "url":
		 * "http://unpidf.univ-paris1.fr/wp-content/themes/wp-creativix/images/logos/1338991426.png"
		 * } }, "coordinates":{ "type": "Point", "lat": "48.627078", "lng":
		 * "2.431309" }, "lang":"fr", "entities":{ "hashtags":[ { "indices":
		 * [28,34], "text": "unpidf" } ], "urls":[ { "indices": [36,65], "url":
		 * "http://unpidf.univ-paris1.fr/", "displayUrl":
		 * "http://unpidf.univ-paris1.fr/", "expandedUrl":
		 * "http://unpidf.univ-paris1.fr/" } ] }, "text":"Une application de l’
		 */
		interface CommentJSON extends Comment {

			@XPath("@id")
			@Override
			String getId();

			@XPath("@url")
			@Override
			String getUrl();

			@XPath("@postedAt")
			@Override
			DateTime getPostedAt();

			@XPath("@displayPostedAt")
			@Override
			String getDisplayPostedAt();

			@XPath("@source")
			@Override
			String getSource();

			@XPath("author/@username")
			@Override
			String getAuthorUsername();

			@XPath("author/@timeZone")
			@Override
			String getAuthorTimeZone();

			@XPath("author/profileImage/@url")
			@Override
			String getAuthorProfileImageUrl();

			@XPath("author/@lang")
			@Override
			@Nullable
			String getAuthorLang();

			boolean isNullAuthorLang();

			@XPath("@text")
			@Override
			String getText();
		}
	}
}
