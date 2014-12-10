package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.admin.GeocampusPoiManageJSONController;
import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.CommentBuilder;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "commentStatus/${uid}" })
public class CommentStatusController extends AbstractBackendController {

	@PathVariable("${uid}")
	private int getCommentUid() {

		return getPathIntVariable("${uid}");
	}

	public CommentStatusController(final TransactionManager tx,
			final CommentDataSource comments,
			final CommentsController commentsController) {

		this.comments = checkNotNull(comments, "comments");
		this.commentsController = checkNotNull(commentsController,
				"commentsController");
		this.tx = checkNotNull(tx, "tx");
	}

	private static final Log log = LogFactory.getLog(CommentStatusController.class);
	
	private final CommentDataSource comments;
	private final CommentsController commentsController;
	private final TransactionManager tx;

	@Override
	public View action() throws Exception {

		// COMMENT

		final Comment comment = comments.getByUid(getCommentUid());

		final Integer uid = comment.getUid();

		final Lock lock = tx.acquireLock(5000, "comments", uid);
		try {

			return commentUpdate(lock, comment);

		} finally {
			lock.release();
		}

	}

	private View commentUpdate(final Lock lock, final Comment comment)
			throws Exception {

		// COMMENT BUILDER

		final CommentBuilder commentBuilder = comments.create();

		commentBuilder.setAuthorName(comment.getAuthorName());
		commentBuilder.setId(comment.getId());
		commentBuilder.setLocalRevfile(comment.getLocalRevfile());
		commentBuilder.setMessage(comment.getMessage());
		commentBuilder.setParentId(comment.getParentId());
		commentBuilder.setPostedAt(comment.getPostedAt());
		commentBuilder.setPostedBy(comment.getPostedBy());
		commentBuilder.setTitle(comment.getTitle());
		commentBuilder.setUid(comment.getUid());
		if (comment.getActive().equals("false"))
			commentBuilder.setActive("true");
		else
			commentBuilder.setActive("false");

		lock.save(commentBuilder);

		lock.commit();
		
		setAttribute("status", true);

		return new View("comments_redirect.jsp");
	}

}
