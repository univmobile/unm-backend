package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.Comment;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.json.JSONList;
import fr.univmobile.backend.json.JSONMap;

public class CommentJSONClientImpl implements CommentJSONClient {

	@Inject
	public CommentJSONClientImpl(@Named("CommentJSONClientImpl")//
			final CommentClient commentClient) {

		this.commentClient = checkNotNull(commentClient, "commentClient");
	}

	private final CommentClient commentClient;

	private static final Log log = LogFactory
			.getLog(CommentJSONClientImpl.class);

	@Override
	public String getCommentsJSONByPoiId(final int poiId) throws IOException,
			SQLException {

		if (log.isDebugEnabled()) {
			log.debug("getCommentsJSONByPoiId():" + poiId + "...");
		}

		final Comment[] comments = commentClient.getCommentsByPoiId(poiId);

		return jsonComments(comments);
	}

	@Override
	public String getMostRecentCommentsJSON(final int limit)
			throws IOException, SQLException {

		if (log.isDebugEnabled()) {
			log.debug("getMostRecentCommentsJSON():" + limit + "...");
		}

		final Comment[] comments = commentClient.getMostRecentComments(limit);

		return jsonComments(comments);
	}

	@Override
	public String searchCommentsJSON(final String query, final int limit)
			throws IOException, SQLException {

		if (log.isDebugEnabled()) {
			log.debug("searchCommentsJSON():" + query + ", " + limit + "...");
		}

		final Comment[] comments = commentClient.searchComments(query, limit);

		return jsonComments(comments);
	}

	private static String jsonComments(final Comment[] comments) {

		final JSONMap json = new JSONMap();

		final JSONList list = new JSONList();

		json.put("comments", list);

		for (final Comment comment : comments) {

			final JSONMap map = new JSONMap() //
					.put("id", comment.getId()) //
					.put("text", comment.getText());

			list.add(map);

			final JSONMap authorMap = new JSONMap()
					.put("username", comment.getAuthorUsername())
					.put("displayName", comment.getAuthorDisplayName())
					.put("profileImage", new JSONMap() //
							.put("url", comment.getAuthorProfileImageUrl()));

			map.put("author", authorMap);
		}

		return json.toJSONString();
	}
}
