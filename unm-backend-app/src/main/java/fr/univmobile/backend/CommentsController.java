package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;
import java.sql.SQLException;

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
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.DataBeans;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
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

	public CommentsController(final TransactionManager tx,
			final CommentDataSource comments,
			final CommentManager commentManager, final UserDataSource users,
			final RegionDataSource regions, final PoiDataSource pois,
			final PoiTreeDataSource poiTrees) {

		super(tx, users, regions, pois, poiTrees);

		this.comments = checkNotNull(comments, "comments");
		this.commentManager = checkNotNull(commentManager, "commentManager");
	}

	private final CommentDataSource comments;
	private final CommentManager commentManager;

	private PoiClient getPoiClient() {

		return new PoiClientFromLocal(getBaseURL(), pois, poiTrees, regions);
	}

	private CommentClient getCommentClient() {

		return new CommentClientFromLocal(getBaseURL(), comments,
				commentManager);
	}

	private static final Log log = LogFactory.getLog(CommentsController.class);

	@Override
	public View action() throws IOException, SQLException,
			TransactionException, ClientException, PageNotFoundException {

		if (!hasCommentsContext()) {

			return mostRecentComments();
		}

		final String context = getCommentsContext();

		if (!context.startsWith("poi")) {
			throw new PageNotFoundException();
		}

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

	private View mostRecentComments() throws SQLException, IOException {

		// 2. COMMENTS

		final Comment[] comments = getCommentClient().getMostRecentComments(80);

		setAttribute("comments", comments);

		// 3. COMMENTS INFO

		setAttribute(
				"commentsInfo",
				DataBeans.instantiate(CommentsInfo.class)
						.setContext("Commentaires les plus récents")
						.setResultCount(comments.length));

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
