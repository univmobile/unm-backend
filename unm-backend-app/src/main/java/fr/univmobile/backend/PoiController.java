package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.Comment;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.client.Poi;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiNotFoundException;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "pois/${id}" })
public class PoiController extends AbstractBackendController {

	@PathVariable("${id}")
	private int getPoiId() {

		return getPathIntVariable("${id}");
	}

	public PoiController(final TransactionManager tx,
			final CommentDataSource comments,
			final CommentThreadDataSource commentThreads,
			final UserDataSource users, final RegionDataSource regions,
			final PoiDataSource pois, final PoiTreeDataSource poiTrees) {

		super(tx, users, regions, pois, poiTrees);

		this.comments = checkNotNull(comments, "commentDataSource");
		this.commentThreads = checkNotNull(commentThreads,
				"commentThreadDataSource");
	}

	private final CommentDataSource comments;
	private final CommentThreadDataSource commentThreads;

	private PoiClient getPoiClient() {

		return new PoiClientFromLocal(getBaseURL(), pois, poiTrees, regions);
	}

	private CommentClient getCommentClient() {
		
		return new CommentClientFromLocal(
				getBaseURL(), comments, commentThreads);
	}

	@Override
	public View action() throws IOException, TransactionException,
			ClientException, PageNotFoundException {

		final int id = getPoiId();

		// 1. POI
		
		final Poi poi;

		try {

			poi = getPoiClient().getPoi(id);

		} catch (final PoiNotFoundException e) {

			throw new PageNotFoundException();
		}

		setAttribute("poi", poi);
		
		// 2. COMMENTS
		
		// Implementation note: Do not set "commentCount" as a property in
		// "poi", since it would mean that each time you fetch some POI info
		// (say, for a list of POIs), you want to fetch its comment count.

		final Comment[] comments = getCommentClient().getCommentsByPoiId(id);

		setAttribute("commentCount", comments.length);

		// 9. END

		return new View("poi.jsp");
	}
}
