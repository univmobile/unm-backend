package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

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
import fr.univmobile.commons.tx.TransactionException;
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

	public PoiController(final CommentDataSource comments,
			final CommentManager commentManager,
			final RegionDataSource regions, final PoiDataSource pois,
			final PoiTreeDataSource poiTrees) {

		this.comments = checkNotNull(comments, "commentDataSource");
		this.commentManager = checkNotNull(commentManager, "commentManager");

		this.pois = checkNotNull(pois, "pois");
		this.poiTrees = checkNotNull(poiTrees, "poiTrees");
		this.regions = checkNotNull(regions, "regions");
	}

	private final RegionDataSource regions;
	private final PoiDataSource pois;
	private final PoiTreeDataSource poiTrees;
	private final CommentDataSource comments;
	private final CommentManager commentManager;

	private PoiClient getPoiClient() {

		return new PoiClientFromLocal(getBaseURL(), pois, poiTrees, regions);
	}

	private CommentClient getCommentClient() {

		return new CommentClientFromLocal(getBaseURL(), comments,
				commentManager);
	}

	@Override
	public View action() throws IOException, SQLException,
			TransactionException, ClientException, PageNotFoundException {

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
