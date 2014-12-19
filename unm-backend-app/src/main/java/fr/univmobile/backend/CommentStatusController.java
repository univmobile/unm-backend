package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.CommentRepository;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "commentStatus/${id}" })
public class CommentStatusController extends AbstractBackendController {

	@PathVariable("${id}")
	private long getCommentUid() {

		return getPathLongVariable("${id}");
	}

	public CommentStatusController(final CommentRepository commentRepository) {
		this.commentRepository = checkNotNull(commentRepository,
				"commentRepository");
	}

	private CommentRepository commentRepository;

	@Override
	public View action() throws Exception {

		// COMMENT

		Comment comment = commentRepository.findOne(getCommentUid());

		return commentUpdate(comment);
	}

	private View commentUpdate(final Comment comment) {

		if (comment.isActive())
			comment.setActive(false);
		else
			comment.setActive(true);

		commentRepository.save(comment);
		
		return new View("comments_redirect.jsp");
	}

}
