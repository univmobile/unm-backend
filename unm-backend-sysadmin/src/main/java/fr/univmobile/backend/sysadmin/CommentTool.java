package fr.univmobile.backend.sysadmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nullable;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.CommentThread.CommentRef;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.backend.core.impl.ConnectionType;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.backend.search.SearchHelper;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

/**
 * Code for the "comment" command-line tool.
 */
class CommentTool extends AbstractTool {

	public CommentTool(final int limit, @Nullable final String query,
			final ConnectionType dbType, final Connection cxn)
			throws IOException, SQLException, ParserConfigurationException {

		super(dbType, cxn);

		this.query = query;
		this.limit = limit;

		final CommentDataSource comments = BackendDataSourceFileSystem
				.newDataSource(CommentDataSource.class,
						getCategoryDir("comments"));

		final LogQueue logQueue = new LogQueueDbImpl(dbType, cxn);

		searchManager = new SearchManagerImpl(logQueue, dbType, cxn);

		commentManager = new CommentManagerImpl(logQueue, comments,
				searchManager, dbType, cxn);
	}

	@Nullable
	private final String query;

	private final int limit;

	private final SearchManager searchManager;
	private final CommentManager commentManager;

	@Override
	public CommentResult run() throws IOException, SQLException, SAXException {

		if (query != null) {

			return runQuery();
		}

		final CommentResult result = new CommentResult();

		int count = 0;

		for (final Comment comment : commentManager
				.getMostRecentComments(limit + 1)) {

			if (count >= limit) {
				System.out.println("...");
				break;
			}

			++count;

			final int uid = comment.getUid();
			final int poiUid = comment.getMainContext().getUid();

			result.addComment(comment);

			System.out.println("#" + count + ": poi=" + poiUid + " uid=" + uid
					+ " " + comment.getPostedAt() + " - "
					+ comment.getPostedBy() + " - " + comment.getMessage());
		}

		return result;
	}

	private CommentResult runQuery() throws IOException, SQLException,
			SAXException {

		final CommentRef[] commentRefs = new SearchHelper(searchManager)
				.search(CommentRef.class, query);

		final CommentResult result = new CommentResult();

		int count = 0;

		for (final CommentRef commentRef : commentRefs) {

			if (count >= limit) {
				System.out.println("...");
				break;
			}

			++count;

			final int uid = commentRef.getUid();

			final Comment comment = commentManager.getByUid(uid);

			final int poiUid = comment.getMainContext().getUid();

			result.addComment(comment);

			System.out.println("#" + count + ": poi=" + poiUid + " uid=" + uid
					+ " " + comment.getPostedAt() + " - "
					+ comment.getPostedBy() + " - " + comment.getMessage());

		}

		return result;
	}
}
