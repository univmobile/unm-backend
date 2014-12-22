package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import fr.univmobile.backend.core.CommentBuilder;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.Paths;


@Paths({ "json/comment/post", "json/comment/post/", "json/comment/post.json" })
public class CommentsPostJSONController extends AbstractJSONController {

	public CommentsPostJSONController(final CommentDataSource commentsDs, //
			final CommentManager commentManager //
	) {
		this.commentsDs = checkNotNull(commentsDs, "commentDataSource");
		this.commentManager = checkNotNull(commentManager, "commentManager");
	}

	private final CommentDataSource commentsDs;
	private final CommentManager commentManager;
	
	private static final Log log = LogFactory.getLog(CommentsPostJSONController.class);
	
	@Override
	public String actionJSON(String baseURL) throws Exception {

		if (!hasDelegationUser()) {
			// Not authenticated - Forbid
			throw new PageNotFoundException("Authorization required"); // TODO: 403
		}
		// 1 HTTP

		final CommentInfo data = getHttpInputs(CommentInfo.class);

		if (!data.isHttpValid()) {
			return "{ \"result\": \"invalid\" }";
		}

		// 2. APPLICATION VALIDATION

		return String.format("{ \"result\": \"%s\" }", (commentSave(data) ? "ok" : "error"));
	}
	
	private boolean commentSave(CommentInfo postedComment) throws IOException,
		TransactionException {

		boolean hasErrors;
		
		log.info("action()...");

		final DateTime now = new DateTime();

		final int poiId = postedComment.poiId();

		final CommentBuilder comment = commentsDs.create();
		comment.setMessage(postedComment.message());
		comment.setTitle(comment.getTitle());
		comment.setPostedAt(now);
		comment.setActive("true");
		comment.setPostedBy(getDelegationUser().getDisplayName());
		comment.setAuthorName(getDelegationUser().getUid());
		comment.setContextUid(poiId);
		
		try {
			LogQueueDbImpl.setPrincipal(getDelegationUser().getUid()); // TODO user? delegation?
			commentManager.addToCommentThreadByPoiId(1, comment); // 1 here is a temporal assignment. Remember poiTree and commentsThreads went again already.
			hasErrors = false;
		} catch (Exception e) {
			log.warn("Error posting a comment");
			log.warn(e);
			hasErrors = true;
		}

		return !hasErrors;
	}


	@HttpMethods({"POST", "GET"})
	interface CommentInfo extends HttpInputs {
		@HttpRequired
		@HttpParameter(trim = true)
		int poiId();

		@HttpRequired
		@HttpParameter
		String message();

		@HttpParameter
		String title();
	}
	
}
