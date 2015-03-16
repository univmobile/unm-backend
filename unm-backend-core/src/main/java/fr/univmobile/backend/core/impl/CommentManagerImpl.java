package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.avcompris.lang.NotImplementedException;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.CommentBuilder;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.CommentThread;
import fr.univmobile.backend.core.SearchEntry;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.SequenceTimeoutException;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;

public class CommentManagerImpl extends AbstractDbManagerImpl implements
		CommentManager {

	public CommentManagerImpl(final LogQueue logQueue,
			final CommentDataSource comments,
			final SearchManager searchManager, final ConnectionType dbType,
			final Connection cxn) throws IOException {

		super(dbType, cxn);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.comments = checkNotNull(comments, "comments");
		this.searchManager = checkNotNull(searchManager, "searchManager");

		// searchManager = new SearchManagerImpl(dbType, cxn);

		tx = TransactionManager.getInstance();

		this.maxCommentUid[0] = //
		calcMaxUid(comments.getAllByInt("uid").keySet());
	}

	public CommentManagerImpl(final LogQueue logQueue,
			final CommentDataSource comments,
			final SearchManager searchManager, final ConnectionType dbType,
			final DataSource ds) throws IOException {

		super(dbType, ds);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.comments = checkNotNull(comments, "comments");
		this.searchManager = checkNotNull(searchManager, "searchManager");

		// searchManager = new SearchManagerImpl(dbType, ds);

		tx = TransactionManager.getInstance();

		this.maxCommentUid[0] = //
		calcMaxUid(comments.getAllByInt("uid").keySet());
	}

	private final LogQueue logQueue;
	private final SearchManager searchManager;

	private static int calcMaxUid(final Iterable<Integer> uids) {

		int maxUid = 0;

		for (final int uid : uids) {

			if (uid > maxUid) {
				maxUid = uid;
			}
		}

		return maxUid;
	}

	private final CommentDataSource comments;
	private final TransactionManager tx;
	private int[] maxCommentUid = new int[1];

	private static final Log log = LogFactory.getLog(CommentManagerImpl.class);

	private int newCommentUid() throws TransactionException {

		final long start = System.currentTimeMillis();

		while (System.currentTimeMillis() < start + 5000) {

			final Lock lock = tx.acquireLock(5000, "comments.uid");
			try {

				final int newUid = maxCommentUid[0] + 1;

				maxCommentUid[0] = newUid;

				if (comments.isNullByUid(newUid)) {

					return newUid;
				}

			} finally {
				lock.release();
			}
		}

		throw new SequenceTimeoutException();
	}

	@Override
	public void addToCommentThreadByPoiId(final int poiId,
			final CommentBuilder comment) throws TransactionException,
			SQLException, IOException {

		checkNotNull(comment, "comment");

		if (comment.isNullPostedAt()) {
			throw new IllegalArgumentException(
					"Comment.postedAt should not be null");
		}
		if (comment.isNullPostedBy()) {
			throw new IllegalArgumentException(
					"Comment.postedBy should not be null");
		}
		if (comment.isNullMessage()) {
			throw new IllegalArgumentException(
					"Comment.message should not be null");
		}

		final int uid = newCommentUid();

		//final Logged logged = logQueue.log(new LoggableString(
		//		"COMMENT:ADD:{uid=%d}", uid));

		final Lock lock = tx.acquireLock(5000, "comments\\poi", poiId);

		comment.setUid(uid);

		lock.save(comment);

		final int STATUS_ACTIVE = 1;

		final String path = comment.getLocalRevfile();

		final int revfileId = executeUpdateGetAutoIncrement("createRevfile",
				"comments", path, comment.getId(), new DateTime(),
				STATUS_ACTIVE);

		final int entityId = executeUpdateGetAutoIncrement("createpkComment",
				revfileId, uid, comment.getPostedAt(), comment.getPostedAt(),
				poiId);

		final SearchEntry searchEntry = new SearchEntry("comments", entityId);

		searchEntry.addField("postedBy", comment.getPostedBy());
		searchEntry.addField("message", comment.getMessage());

		searchManager.inject(searchEntry);

		lock.commit();

		//logQueue.log(logged, new LoggableString(
		//		"COMMENT:ADD:%s:{uid=%d, entity_id:%d}", logged, uid, entityId));
	}

	@Override
	public Comment[] getMostRecentComments(final int limit)
			throws SQLException, IOException {

		final List<Comment> comments = new ArrayList<Comment>();

		final Connection cxn = getConnection();
		try {
			final PreparedStatement pstmt = cxn
					.prepareStatement(getSql("getMostRecentCommentUids"));
			try {
				pstmt.setInt(1, limit); // LIMIT

				final ResultSet rs = pstmt.executeQuery();
				try {
					
					while (rs.next()) {

						final int uid = rs.getInt(1);
						
						final Comment comment = this.comments.getByUid(uid);
						
						comments.add(comment);
					}

				} finally {
					rs.close();
				}
			} finally {
				pstmt.close();
			}
		} finally {
			cxn.close();
		}

		return Iterables.toArray(comments, Comment.class);
	}

	@Override
	public Comment getByUid(final int uid) {

		return comments.getByUid(uid);
	}

	@Override
	public <K> Map<K, CommentThread> getAllBy(final Class<K> keyClass,
			final String attributeName) {

		throw new NotImplementedException();
	}

	@Override
	public Map<String, CommentThread> getAllByString(final String attributeName) {

		throw new NotImplementedException();
	}

	@Override
	public Map<Integer, CommentThread> getAllByInt(final String attributeName) {

		throw new NotImplementedException();
	}

	@Override
	public int sizeOfThreads() throws SQLException {

		return executeQueryGetInt("sizeOfCommentThreads");
	}

	@Override
	public int sizeOfCommentsByPoiId(int poiId) throws SQLException {

		return executeQueryGetInt("sizeOfCommentsByPoiId", poiId);
	}

	@Override
	public CommentThread getByPoiId(int poiId) throws SQLException, IOException {

		if (log.isInfoEnabled()) {
			log.info("getByPoiId():" + poiId + "...");
		}

		final String sql = getSql("getCommentsByPoiId");

		final List<Integer> uids = new ArrayList<Integer>();

		final Connection cxn = getConnection();
		try {
			final PreparedStatement pstmt = cxn.prepareStatement(sql);
			try {

				pstmt.setInt(1, poiId);

				final ResultSet rs = pstmt.executeQuery();
				try {

					while (rs.next()) {

						final int uid = rs.getInt(1);

						uids.add(uid);
					}

				} finally {
					rs.close();
				}
			} finally {
				pstmt.close();
			}
		} finally {
			cxn.close();
		}

		return new CommentThreadImpl(uids);
	}

	@Override
	public boolean isNullByPoiId(int poiId) throws SQLException {

		return sizeOfCommentsByPoiId(poiId) == 0;
	}

	private static class CommentThreadImpl implements CommentThread {

		public CommentThreadImpl(final Collection<Integer> uids) {

			commentRefs = new CommentRef[uids.size()];

			int i = 0;

			for (final int uid : uids) {

				commentRefs[i] = new CommentRefImpl(uid);

				++i;
			}
		}

		private final CommentRef[] commentRefs;

		@Override
		public CommentRef[] getAllComments() {

			return commentRefs;
		}

		@Override
		public int sizeOfAllComments() {

			return commentRefs.length;
		}
	}

	private static class CommentRefImpl implements CommentThread.CommentRef {

		public CommentRefImpl(final int uid) {

			this.uid = uid;
		}

		private final int uid;

		@Override
		public int getUid() {

			return uid;
		}

		@Override
		public String getEntryRefId() {

			return Integer.toString(uid);
		}

		@Override
		public final String getCategory() {

			return "comments";
		}
	}
}
