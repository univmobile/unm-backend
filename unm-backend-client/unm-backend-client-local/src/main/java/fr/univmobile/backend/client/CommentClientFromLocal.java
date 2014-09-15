package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.CommentThread;
import fr.univmobile.backend.core.CommentThread.CommentRef;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.search.SearchHelper;
import fr.univmobile.commons.DataBeans;

public class CommentClientFromLocal extends AbstractClientFromLocal implements
		CommentClient {

	@Inject
	public CommentClientFromLocal(final String baseURL,
			final CommentDataSource commentDataSource,
			final CommentManager commentManager,
			final SearchManager searchManager) {

		super(baseURL);

		this.commentDataSource = checkNotNull(commentDataSource,
				"commentDataSource");
		this.commentManager = checkNotNull(commentManager, "commentManager");
		this.searchHelper = new SearchHelper(checkNotNull(searchManager,
				"searchManager"));
	}

	private final CommentDataSource commentDataSource;
	private final CommentManager commentManager;
	private final SearchHelper searchHelper;

	private static final Log log = LogFactory
			.getLog(CommentClientFromLocal.class);

	@Override
	public Comment[] getCommentsByPoiId(final int poiId) throws IOException,
			SQLException {

		if (log.isDebugEnabled()) {
			log.debug("getCommentsByPoiId():" + poiId + "...");
		}

		if (commentManager.isNullByPoiId(poiId)) {
			return new Comment[0];
		}

		final CommentThread commentThread = commentManager.getByPoiId(poiId);

		final CommentRef[] commentRefs = commentThread.getAllComments();

		return getComments(commentRefs);
	}

	private Comment[] getComments(final CommentRef[] commentRefs) {

		final Comment[] comments = new Comment[commentRefs.length];

		for (int i = 0; i < commentRefs.length; ++i) {

			final CommentRef commentRef = commentRefs[i];

			final int uid = commentRef.getUid();

			comments[i] = getComment(commentDataSource.getByUid(uid));
		}

		return comments;
	}

	private static Comment getComment(
			final fr.univmobile.backend.core.Comment dsComment) {

		final DateTime postedAt = dsComment.getPostedAt();

		final MutableComment comment = DataBeans //
				.instantiate(MutableComment.class) //
				.setId(Integer.toString(dsComment.getUid())) //
				.setAuthorUsername(dsComment.getAuthorName()) //
				.setAuthorDisplayName(dsComment.getPostedBy()) //
				.setText(dsComment.getMessage()) //
				.setPostedAt(postedAt) //
				.setDisplayFullPostedAt(formatDateFull(postedAt)) //
				.setDisplayPostedAt(formatDate(postedAt)) //
				.setDisplayPostedAtTime(formatTime(postedAt)); //

		return comment;
	}

	private Comment[] getComments(
			final fr.univmobile.backend.core.Comment[] dsComments) {

		final Comment[] comments = new Comment[dsComments.length];

		for (int i = 0; i < dsComments.length; ++i) {

			comments[i] = getComment(dsComments[i]);
		}

		return comments;
	}

	@Override
	public Comment[] getMostRecentComments(final int limit) throws IOException,
			SQLException {

		if (log.isDebugEnabled()) {
			log.debug("getMostRecentComments():" + limit + "...");
		}

		final fr.univmobile.backend.core.Comment[] dsComments = commentManager
				.getMostRecentComments(limit);

		return getComments(dsComments);
	}

	@Override
	public Comment[] searchComments(final String query, final int limit)
			throws IOException, SQLException {

		if (log.isDebugEnabled()) {
			log.debug("searchComments():" + query + "...");
		}

		final CommentRef[] commentRefs = searchHelper.search(CommentRef.class,
				query);

		final Comment[] comments = new Comment[Math.min(limit,
				commentRefs.length)];

		for (int i = 0; i < comments.length; ++i) {

			final int uid = commentRefs[i].getUid();

			comments[i] = getComment(commentDataSource.getByUid(uid));
		}

		return comments;
	}

	private interface MutableComment extends Comment {

		MutableComment setId(String id);

		MutableComment setUrl(String url);

		MutableComment setText(String text);

		MutableComment setPostedAt(DateTime date);

		MutableComment setDisplayPostedAt(String date);

		MutableComment setDisplayFullPostedAt(String date);

		MutableComment setDisplayPostedAtTime(String date);

		MutableComment setAuthorUsername(@Nullable String address);

		MutableComment setAuthorDisplayName(@Nullable String phone);

		MutableComment setFloor(@Nullable String floor);
	}
}
