package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;

public class CommentTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir_comments = new File(
				"src/test/data/comments/001");

		dataDir_comments = new File("target/CommentTest_comments");

		if (dataDir_comments.isDirectory()) {
			FileUtils.forceDelete(dataDir_comments);
		}

		FileUtils.copyDirectory(originalDataDir_comments, dataDir_comments);

		comments = BackendDataSourceFileSystem.newDataSource(
				CommentDataSource.class, dataDir_comments);

		final File originalDataDir_comment_threads = new File(
				"src/test/data/comment_threads/001");

		dataDir_comment_threads = new File("target/CommentTest_comment_threads");

		if (dataDir_comment_threads.isDirectory()) {
			FileUtils.forceDelete(dataDir_comment_threads);
		}

		FileUtils.copyDirectory(originalDataDir_comment_threads,
				dataDir_comment_threads);

		commentThreads = BackendDataSourceFileSystem.newDataSource(
				CommentThreadDataSource.class, dataDir_comment_threads);
	}

	private CommentDataSource comments;
	private CommentThreadDataSource commentThreads;
	private File dataDir_comments;
	private File dataDir_comment_threads;

	private final TransactionManager tx = TransactionManager.getInstance();

	private static int getFileCount(final File dir) throws IOException {

		return dir.listFiles().length;
	}

	@Test
	public void test_count() throws Exception {

		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(1, commentThreads.getAllBy(Integer.class, "uid").size());
		assertEquals(3, getFileCount(dataDir_comments));
		assertEquals(1, getFileCount(dataDir_comment_threads));
	}

	@Test
	public void testCreateComment_new_thread_3792() throws Exception {

		final int POI_UID = 3792;

		final Lock lock = tx.acquireLock(5000, "comments\\poi", POI_UID);

		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(1, commentThreads.getAllBy(Integer.class, "uid").size());
		assertEquals(3, getFileCount(dataDir_comments));
		assertEquals(1, getFileCount(dataDir_comment_threads));

		final CommentBuilder comment = comments.create();
		final CommentThreadBuilder commentThread = commentThreads.create();

		comment.setUid(45);
		comment.setMessage("Hello World!");

		commentThread.setUid(2);

		commentThread.getContent().addToComments().setUid(comment.getUid());
		commentThread
				.getContent()
				.addToContexts()
				.setId("fr.univmobile:unm-backend:test/pois/001:poi" + POI_UID
						+ "_1").setType("local:poi").setUid(POI_UID);

		lock.save(comment);
		lock.save(commentThread);

		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(1, commentThreads.getAllBy(Integer.class, "uid").size());
		assertEquals(4, getFileCount(dataDir_comments));
		assertEquals(2, getFileCount(dataDir_comment_threads));

		lock.commit();

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentThreads.getAllBy(Integer.class, "uid").size());
		assertEquals(4, getFileCount(dataDir_comments));
		assertEquals(2, getFileCount(dataDir_comment_threads));

		comments.reload();
		commentThreads.reload();

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentThreads.getAllBy(Integer.class, "uid").size());

		final CommentThread newThread = commentThreads.getByPoiId(POI_UID);

		assertEquals(1, newThread.getAllComments().length);
		assertEquals(45, newThread.getAllComments()[0].getUid());
	}

	@Test
	public void testCreateComment_existing_thread_415() throws Exception {

		final int POI_UID = 415;
		
		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(1, commentThreads.getAllBy(Integer.class, "uid").size());
		assertEquals(3, getFileCount(dataDir_comments));
		assertEquals(1, getFileCount(dataDir_comment_threads));

		final CommentThread existingThread = commentThreads.getByPoiId(POI_UID);

		assertEquals(2, existingThread.getAllComments().length);
		assertEquals(1, existingThread.getAllComments()[0].getUid());
		assertEquals(2, existingThread.getAllComments()[1].getUid());

		final Lock lock = tx.acquireLock(5000, "comments\\poi", POI_UID);

		final CommentBuilder comment = comments.create();

		assertTrue(commentThreads.isNullByUid(POI_UID)); // Typo
		assertFalse(commentThreads.isNullByPoiId(POI_UID));

		final CommentThreadBuilder commentThread = commentThreads
				.update(commentThreads.getByPoiId(POI_UID));

		comment.setUid(45);
		comment.setMessage("Hello World!");

		commentThread.getContent().addToComments().setUid(comment.getUid());

		lock.save(comment);
		lock.save(commentThread);

		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(1, commentThreads.getAllBy(Integer.class, "uid").size());
		assertEquals(4, getFileCount(dataDir_comments));
		assertEquals(2, getFileCount(dataDir_comment_threads));

		lock.commit();

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(1, commentThreads.getAllBy(Integer.class, "uid").size());
		assertEquals(4, getFileCount(dataDir_comments));
		assertEquals(2, getFileCount(dataDir_comment_threads));

		comments.reload();
		commentThreads.reload();

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(1, commentThreads.getAllBy(Integer.class, "uid").size());

		final CommentThread newThread = commentThreads.getByPoiId(POI_UID);

		assertEquals(3, newThread.getAllComments().length);
		assertEquals(1, newThread.getAllComments()[0].getUid());
		assertEquals(2, newThread.getAllComments()[1].getUid());
		assertEquals(45, newThread.getAllComments()[2].getUid());
	}
}
