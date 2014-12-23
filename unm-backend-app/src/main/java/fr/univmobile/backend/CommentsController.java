package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.CommentRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.User;
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

	public CommentsController(final CommentRepository commentRepository,
			final PoiRepository poiRepository) {
		this.commentRepository = checkNotNull(commentRepository,
				"commentRepository");
		this.poiRepository = checkNotNull(poiRepository, "poiRepository");
	}

	private CommentRepository commentRepository;
	private PoiRepository poiRepository;

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

		Long poiId;
		try {

			poiId = Long.parseLong(substringAfter(context, "poi"));

		} catch (final NumberFormatException e) {
			throw new PageNotFoundException();
		}

		if (log.isDebugEnabled()) {
			log.debug("Found poiId: " + poiId);
		}

		// 1. POI

		Poi poi = poiRepository.findOne(poiId);

		setAttribute("poi", poi);

		// 2. COMMENTS

		Iterable<Comment> allComments = commentRepository.findByPoi(poi);

		List<Comment> comments = new ArrayList<Comment>();

		for (Comment c : allComments)
			comments.add(c);

		setAttribute("comments", comments);

		// 3. COMMENTS INFO

		final CommentsInfo commentsInfo = instantiate(CommentsInfo.class)
				.setContext(
						"Commentaires pour le POI " + poiId + " : "
								+ poi.getName())
				.setResultCount(comments.size());

		setAttribute("commentsInfo", commentsInfo);

		// 9. END

		return new View("comments.jsp");
	}

	private View mostRecentComments() {

		final String context;

		final SearchQuery query = getHttpInputs(SearchQuery.class);

		List<Comment> comments = new ArrayList<Comment>();

		if (query.isHttpValid()) {

			final String q = query.query();

			setAttribute("query", q);

			context = "Query: " + q;

			Iterable<Comment> allComments = commentRepository.findAll();

			for (Comment c : allComments)
				comments.add(c);

		} else {

			context = "Commentaires les plus récents";

			Iterable<Comment> allComments = commentRepository.findAll();

			for (Comment c : allComments)
				comments.add(c);
		}

		CommentsInfo commentsInfo;

		User dUser = getDelegationUser();

		if (dUser.getRole().equals(User.ADMIN)) {

			List<Comment> auxComments = new ArrayList<Comment>();

			for (Comment c : comments)
				if (dUser.getUniversity().getId()
						.equals(c.getPoi().getUniversity().getId()))
					auxComments.add(c);

			setAttribute("comments", auxComments.toArray());

			commentsInfo = instantiate(CommentsInfo.class) //
					.setContext(context) //
					.setResultCount(auxComments.size());

			setAttribute("commentsInfo", commentsInfo);

		} else {
			setAttribute("comments", comments);

			commentsInfo = instantiate(CommentsInfo.class) //
					.setContext(context) //
					.setResultCount(comments.size());

			setAttribute("commentsInfo", commentsInfo);
		}

		return new View("comments.jsp");
	}
}

interface CommentsInfo {

	int getResultCount();

	CommentsInfo setResultCount(int resultCount);

	String getContext();

	CommentsInfo setContext(String context);
}

@HttpMethods("GET")
interface SearchQuery extends HttpInputs {

	@HttpRequired
	@HttpParameter("q")
	String query();

}
