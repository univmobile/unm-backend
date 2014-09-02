package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.CommentJSONClient;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/comments/${context}" })
public class CommentsJSONController extends AbstractJSONController {

	@PathVariable("${context}")
	private String getCommentsContext() {

		return getPathStringVariable("${context}");
	}

	public CommentsJSONController(final PoiDataSource pois,
			final CommentJSONClient commentJSONClient) {

		this.pois = checkNotNull(pois, "pois");
		this.commentJSONClient = checkNotNull(commentJSONClient,
				"commentJSONClient");
	}

	private final PoiDataSource pois;
	private final CommentJSONClient commentJSONClient;

	private static final Log log = LogFactory
			.getLog(CommentsJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException,
			TransactionException, PageNotFoundException {

		log.debug("actionJSON()...");

		String poiIdStr = substringAfter(getCommentsContext(), "poi");

		if (poiIdStr.endsWith(".json")) {
			poiIdStr = substringBeforeLast(poiIdStr, ".json");
		}

		if (poiIdStr.contains("/")) {
			poiIdStr = substringBefore(poiIdStr, "/");
		}

		final int poiId;

		try {
			poiId = Integer.parseInt(poiIdStr);
		} catch (final NumberFormatException e) {
			throw new PageNotFoundException();
		}

		if (log.isDebugEnabled()) {
			log.debug("Found poiId: " + poiId);
		}

		if (pois.isNullByUid(poiId)) {
			throw new PageNotFoundException("poiId: " + poiId);
		}

		final String commentsJSON = commentJSONClient
				.getCommentsJSONByPoiId(poiId);

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/comments/poi" + poiId) + "\","
				+ substringAfter(commentsJSON, "{");

		return json;
	}
}
