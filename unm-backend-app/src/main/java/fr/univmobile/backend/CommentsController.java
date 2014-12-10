package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.Comment;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.client.Poi;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiNotFoundException;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.commons.DataBeans;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "comments", "comments/", "comments/${context}" })
public class CommentsController extends AbstractBackendController {

	@PathVariable("${context}")
	private String getCommentsContext() {

		return getPathStringVariable("${context}");
	}

	private boolean hasCommentsContext() {

		return hasPathStringVariable("${context}");
	}

	public CommentsController(final CommentDataSource comments,
			final CommentManager commentManager,
			final SearchManager searchManager, final RegionDataSource regions,
			final PoiDataSource pois) {

		this.comments = checkNotNull(comments, "comments");
		this.commentManager = checkNotNull(commentManager, "commentManager");
		this.searchManager = checkNotNull(searchManager, "searchManager");
		this.pois = checkNotNull(pois, "pois");
		this.regions = checkNotNull(regions, "regions");
	}

	private final RegionDataSource regions;
	private final PoiDataSource pois;
	private final CommentDataSource comments;
	private final CommentManager commentManager;
	private final SearchManager searchManager;

	private PoiClient getPoiClient() {

		return new PoiClientFromLocal(getBaseURL(), pois, regions);
	}

	private CommentClient getCommentClient() {

		return new CommentClientFromLocal(getBaseURL(), comments,
				commentManager, searchManager);
	}

	private static final Log log = LogFactory.getLog(CommentsController.class);

	@Override
	public View action() throws Exception {

		if (!hasCommentsContext()) {

			return mostRecentComments();
		}

		final String context = getCommentsContext();

		if (context.startsWith("poi")) {

			return poiComments(context);
		}

		throw new PageNotFoundException();
	}

	private View poiComments(final String context) throws Exception {

		final int poiId;

		try {

			poiId = Integer.parseInt(substringAfter(context, "poi"));

		} catch (final NumberFormatException e) {
			throw new PageNotFoundException();
		}

		if (log.isDebugEnabled()) {
			log.debug("Found poiId: " + poiId);
		}

		// 1. POI

		final Poi poi;

		try {

			poi = getPoiClient().getPoi(poiId);

		} catch (final PoiNotFoundException e) {
			throw new PageNotFoundException();
		}

		setAttribute("poi", poi);

		// 2. COMMENTS

		final Comment[] comments = getCommentClient().getCommentsByPoiId(poiId);

		setAttribute("comments", comments);

		// 3. COMMENTS INFO

		setAttribute(
				"commentsInfo",
				DataBeans
						.instantiate(CommentsInfo.class)
						.setContext(
								"Commentaires pour le POI " + poiId + " : "
										+ poi.getName())
						.setResultCount(comments.length));

		// 9. END

		return new View("comments.jsp");
	}

	private View mostRecentComments() throws SQLException, IOException,
			ClientException {

		final Comment[] clientComments;

		final String context;

		final SearchQuery query = getHttpInputs(SearchQuery.class);

		if (query.isHttpValid()) {

			final String q = query.query();

			setAttribute("query", q);

			context = "Query: " + q;

			clientComments = getCommentClient().searchComments(q, 100);

		} else {

			context = "Commentaires les plus récents";

			clientComments = getCommentClient().getMostRecentComments(80);
		}

		if (getDelegationUser().getRole().equals("admin")) {

			List<Comment> auxComments = new ArrayList<Comment>();

			for (Comment c : clientComments) {
				fr.univmobile.backend.core.Poi p = pois.getByUid(c
						.getContextUid());
				if (p.getUniversityIds().length > 0)
					if (p.getUniversityIds()[0].equals(getDelegationUser()
							.getPrimaryUniversity()))
						auxComments.add(c);
			}

			setAttribute("comments", auxComments.toArray());
		} else
			setAttribute("comments", clientComments);

		// 3. COMMENTS INFO

		setAttribute("commentsInfo", DataBeans.instantiate(CommentsInfo.class)
				.setContext(context).setResultCount(clientComments.length));

		// 9. END

		return new View("comments.jsp");
	}

	private static interface CommentsInfo {

		int getResultCount();

		CommentsInfo setResultCount(int resultCount);

		String getContext();

		CommentsInfo setContext(String context);
	}
}

@HttpMethods("GET")
interface SearchQuery extends HttpInputs {

	@HttpRequired
	@HttpParameter("q")
	String query();
}
