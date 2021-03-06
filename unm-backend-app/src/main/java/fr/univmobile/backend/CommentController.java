package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import fr.univmobile.backend.core.CommentBuilder;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "comment" })
public class CommentController extends AbstractBackendController {

	public CommentController(final CommentDataSource comments, //
			final CommentManager commentManager //
	) {
		this.comments = checkNotNull(comments, "commentDataSource");
		this.commentManager = checkNotNull(commentManager, "commentManager");
	}

	private final CommentDataSource comments;
	private final CommentManager commentManager;

	@Override
	public View action() throws IOException, SQLException, TransactionException {

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
