package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.CommentRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/comment/post", "json/comment/post/", "json/comment/post.json" })
public class CommentsPostJSONController extends AbstractJSONController {

	public CommentsPostJSONController(
			final CommentRepository commentRepository,
			final PoiRepository poiRepository) {
		this.commentRepository = checkNotNull(commentRepository,
				"commentRepository");
		this.poiRepository = checkNotNull(poiRepository, "poiRepository");
	}

	private CommentRepository commentRepository;
	private PoiRepository poiRepository;

	private static final Log log = LogFactory
			.getLog(CommentsPostJSONController.class);

	@Override
	public String actionJSON(String baseURL) throws Exception {

		if (!hasDelegationUser()) {
			// Not authenticated - Forbid
			throw new PageNotFoundException("Autorisation requise");
		}

		// 1 HTTP

		final CommentInfo data = getHttpInputs(CommentInfo.class);

		if (!data.isHttpValid()) {
			return "{ \"result\": \"invalid\" }";
		}

		// 2. APPLICATION VALIDATION

		return String.format("{ \"result\": \"%s\" }",
				(commentSave(data) ? "ok" : "error"));
	}

	private boolean commentSave(CommentInfo form) throws IOException {

		boolean hasErrors = false;
		log.info("action()...");

		final Long poiId = form.poiId();

		Poi p = null;

		if (poiId != null)
			p = poiRepository.findOne(poiId);
		else
			hasErrors = true;

		if (p != null) {

			if (!getDelegationUser().isSuperAdmin())
				if (getDelegationUser().getUniversity().getId() != p
						.getUniversity().getId())
					return false;

			Comment comment = new Comment();
			comment.setMessage(form.message());
			comment.setTitle(form.title());
			comment.setActive(true);
			comment.setPoi(poiRepository.findOne(poiId));

			commentRepository.save(comment);

		} else
			hasErrors = true;

		return !hasErrors;
	}

	@HttpMethods({ "POST", "GET" })
	interface CommentInfo extends HttpInputs {

		@HttpRequired
		@HttpParameter
		Long poiId();

		@HttpRequired
		@HttpParameter
		String message();

		@HttpParameter
		String title();
	}

}
