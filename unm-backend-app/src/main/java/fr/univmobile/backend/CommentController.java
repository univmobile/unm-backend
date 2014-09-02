package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import fr.univmobile.backend.core.CommentBuilder;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "comment" })
public class CommentController extends AbstractBackendController {

	public CommentController(final TransactionManager tx, //
			final CommentDataSource comments, //
			final CommentThreadDataSource commentThreads, //
			final UserDataSource users, //
			final RegionDataSource regions, //
			final PoiDataSource pois, //
			final PoiTreeDataSource poiTrees //
	) {
		super(tx, users, regions, pois, poiTrees);

		this.comments = checkNotNull(comments, "commentDataSource");
		this.commentThreads = checkNotNull(commentThreads,
				"commentThreadDataSource");
	}

	private final CommentDataSource comments;
	private final CommentThreadDataSource commentThreads;

	@Override
	public View action() throws IOException, TransactionException {

		log.info("action()...");

		final DateTime now = new DateTime();

		final PostComment postComment = getHttpInputs(PostComment.class);

		if (!postComment.isHttpValid()) {

			if (isHttpGet()) {

				getDelegationUser(); // Check that the user is authentified.

				return new View("text/plain", UTF_8, Locale.ENGLISH,
						"comment_OK.jsp");
			}

			throw new IllegalArgumentException("Invalid form");
		}

		if (!isHttpPost()) {
			throw new IllegalStateException("HTTP form method should be POST");
		}

		final int poiId = postComment.poi_id();
		final String username = postComment.username();
		final String message = postComment.message();

		if (log.isInfoEnabled()) {
			log.info("addComment(): username=" + username //
					+ ", poi_id=" + poiId //
					+ ", message=" + message);
		}

		final CommentBuilder comment = comments.create();

		comment.setAuthorName(getDelegationUser().getUid());

		final CommentManager commentManager = new CommentManagerImpl(comments,
				commentThreads);

		comment.setMessage(message);
		comment.setPostedBy(getDelegationUser().getDisplayName());
		comment.setPostedAt(now);

		commentManager.addToCommentThreadByPoiId(poiId, comment);

		setAttribute("commentId", comment.getUid());

		return new View("text/plain", UTF_8, Locale.ENGLISH, "comment_OK.jsp");
	}

	private static final Log log = LogFactory.getLog(CommentController.class);

	@HttpMethods({ "POST", "GET" })
	interface PostComment extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String username();

		@HttpRequired
		@HttpParameter
		int poi_id();

		@HttpRequired
		@HttpParameter
		String message();
	}
}
