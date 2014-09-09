package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.avcompris.lang.NotImplementedException;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.CommentBuilder;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.SequenceTimeoutException;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;

public class CommentManagerImpl extends AbstractDbManagerImpl implements
		CommentManager {

	public CommentManagerImpl(final CommentDataSource comments,
			final ConnectionType dbType, final Connection cxn)
			throws IOException {

		super(dbType, cxn);

		this.comments = checkNotNull(comments, "comments");
		// this.commentThreads = checkNotNull(commentThreads, "commentThreads");

		tx = TransactionManager.getInstance();

		this.maxCommentUid[0] = //
		calcMaxUid(comments.getAllByInt("uid").keySet());

		// this.maxCommentThreadUid[0] = //
		// calcMaxUid(commentThreads.getAllByInt("uid").keySet());
	}

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
	// private final CommentThreadDataSource commentThreads;
	private final TransactionManager tx;
	private int[] maxCommentUid = new int[1];

	// private int[] maxCommentThreadUid = new int[1];

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

	private int newCommentThreadUid() throws TransactionException {

		/*
		 * final long start = System.currentTimeMillis();
		 * 
		 * while (System.currentTimeMillis() < start + 5000) {
		 * 
		 * final Lock lock = tx.acquireLock(5000, "comment_threads.uid"); try {
		 * 
		 * final int newUid = maxCommentThreadUid[0] + 1;
		 * 
		 * maxCommentThreadUid[0] = newUid;
		 * 
		 * if (comments.isNullByUid(newUid)) {
		 * 
		 * return newUid; }
		 * 
		 * } finally { lock.release(); } }
		 * 
		 * throw new SequenceTimeoutException();
		 */

		throw new NotImplementedException();
	}

	@Override
	public void addToCommentThreadByPoiId(final int poiId,
			final CommentBuilder comment) throws TransactionException {

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

		/*
		 * final Lock lock = tx.acquireLock(5000, "comments\\poi", poiId);
		 * 
		 * final CommentThreadBuilder commentThread;
		 * 
		 * if (commentThreads.isNullByPoiId(poiId)) {
		 * 
		 * //commentThread = commentThreads.create();
		 * 
		 * //commentThread.setUid(newCommentThreadUid());
		 * 
		 * commentThread .getContent() .addToContexts()
		 * .setId("fr.univmobile:unm-backend:test/pois/001:poi" + poiId +
		 * "_1").setType("local:poi").setUid(poiId);
		 * 
		 * } else {
		 * 
		 * commentThread = commentThreads.update(commentThreads
		 * .getByPoiId(poiId)); }
		 * 
		 * final int uid = newCommentUid();
		 * 
		 * comment.setUid(uid);
		 * 
		 * commentThread.getContent().addToComments().setUid(uid);
		 * 
		 * lock.save(comment); lock.save(commentThread);
		 * 
		 * lock.commit();
		 */

		throw new NotImplementedException();
	}

	@Override
	public Comment[] getMostRecentComments(final int limit)
			throws SQLException, IOException {

		final List<Comment> comments = new ArrayList<Comment>();

		final PreparedStatement pstmt = cxn
				.prepareStatement(getSql("getMostRecentCommentUids"));
		try {
			pstmt.setInt(1, limit); // LIMIT

			final ResultSet rs = pstmt.executeQuery();
			try {

				while (rs.next()) {

					final int uid = rs.getInt(1);

					final Comment comment = this.comments.getByUid(uid);

					// final int uid = comment.getUid();
					// final int poiUid = comment.getMainContext().getUid();

					comments.add(comment);
				}

			} finally {
				rs.close();
			}

		} finally {
			pstmt.close();
		}

		return Iterables.toArray(comments, Comment.class);
	}
}
