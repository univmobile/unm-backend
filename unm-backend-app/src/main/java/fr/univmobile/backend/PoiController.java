package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;

import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.CommentRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "pois/${id}" })
public class PoiController extends AbstractBackendController {

	@PathVariable("${id}")
	private long getPoiId() {

		return getPathIntVariable("${id}");
	}

	public PoiController(final PoiRepository poiRepository,
			final CommentRepository commentRepository) {
		this.poiRepository = checkNotNull(poiRepository, "poiRepository");
		this.commentRepository = checkNotNull(commentRepository,
				"commentRepository");
	}

	private final PoiRepository poiRepository;
	private final CommentRepository commentRepository;

	@Override
	public View action() throws IOException {
		if (getDelegationUser().isLibrarian()) {
			return sendError403("FORBIDDEN");
		}

		final Long poiId = getPoiId();

		// 1. POI

		final Poi poi = poiRepository.findOne(poiId);
		setAttribute("poi", poi);

		// 2. COMMENTS

		// Implementation note: Do not set "commentCount" as a property in
		// "poi", since it would mean that each time you fetch some POI info
		// (say, for a list of POIs), you want to fetch its comment count.

		final Collection<Comment> comments = commentRepository.findByPoi(poi);

		setAttribute("commentCount", comments.size());

		// 9. END

		return new View("poi.jsp");
	}
}