package fr.univmobile.backend.core;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.CommentThread.CommentRef;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.backend.core.impl.IndexationImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.search.SearchHelper;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class CommentTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir_users = new File("src/test/data/users/001");
		final File originalDataDir_regions = new File(
				"src/test/data/regions/001");
		final File originalDataDir_pois = new File("src/test/data/pois/001");
		final File originalDataDir_comments = new File(
				"src/test/data/comments/001");

		dataDir_comments = new File("target/CommentTest_comments");

		if (dataDir_comments.isDirectory()) {
			FileUtils.forceDelete(dataDir_comments);
		}

		FileUtils.copyDirectory(originalDataDir_comments, dataDir_comments);

		comments = BackendDataSourceFileSystem.newDataSource(
				CommentDataSource.class, dataDir_comments);

		final File dataDir_users = new File("target/CommentTest_users");

		if (dataDir_users.isDirectory()) {
			FileUtils.forceDelete(dataDir_users);
		}

		FileUtils.copyDirectory(originalDataDir_users, dataDir_users);

		final File dataDir_regions = new File("target/CommentTest_regions");

		if (dataDir_regions.isDirectory()) {
			FileUtils.forceDelete(dataDir_regions);
		}

		FileUtils.copyDirectory(originalDataDir_regions, dataDir_regions);

		final File dataDir_pois = new File("target/CommentTest_pois");

		if (dataDir_pois.isDirectory()) {
			FileUtils.forceDelete(dataDir_pois);
		}

		FileUtils.copyDirectory(originalDataDir_pois, dataDir_pois);

		final File dbFile = new File("target/CommentTest.h2.db");

		FileUtils.deleteQuietly(dbFile);

		assertFalse(dbFile.exists());

		final String url = "jdbc:h2:./target/CommentTest";

		cxn = DriverManager.getConnection(url);

		final SearchManager searchManager = new SearchManagerImpl(H2, cxn);

		final Indexation indexation = new IndexationImpl(dataDir_users,
				dataDir_regions, dataDir_pois, dataDir_comments, searchManager,
				H2, cxn);

		indexation.indexData(null);

		commentManager = new CommentManagerImpl(comments, searchManager, H2,
				cxn);

		pois = BackendDataSourceFileSystem.newDataSource(PoiDataSource.class,
				dataDir_pois);

		searchHelper = new SearchHelper(new SearchManagerImpl(H2, cxn));
	}

	private CommentDataSource comments;
	private CommentManager commentManager;
	private SearchHelper searchHelper;
	private File dataDir_comments;
	private PoiDataSource pois;
	private Connection cxn;

	@After
	public void tearDown() throws Exception {

		if (cxn != null) {

			cxn.close();

			cxn = null;
		}
	}

	// private final TransactionManager tx = TransactionManager.getInstance();

	private static int getFileCount(final File dir) throws IOException {

		return dir.listFiles().length;
	}

	@Test
	public void test_count() throws Exception {

		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(3, getFileCount(dataDir_comments));
	}

	@Test
	public void testCommentManager_createComment_new_thread_3792()
			throws Exception {

		final int POI_UID = 1; // 3792;

		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(3, getFileCount(dataDir_comments));

		final CommentThread oldThread = commentManager.getByPoiId(POI_UID);
		assertEquals(0, oldThread.getAllComments().length);
		assertEquals(0, oldThread.sizeOfAllComments());

		final CommentBuilder comment = comments.create();

		comment.setMessage("Hello World!");
		comment.setPostedAt(new DateTime());
		comment.setPostedBy("Dummy");

		commentManager.addToCommentThreadByPoiId(POI_UID, comment);

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(3, commentManager.sizeOfThreads());
		assertEquals(4, getFileCount(dataDir_comments));

		comments.reload();

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(3, commentManager.sizeOfThreads());

		final CommentThread newThread = commentManager.getByPoiId(POI_UID);

		assertEquals(1, newThread.getAllComments().length);
		assertEquals(1, newThread.sizeOfAllComments());
		final int uid = newThread.getAllComments()[0].getUid();
		assertEquals("Hello World!", comments.getByUid(uid).getMessage());
	}

	@Test
	public void testCommentManager_createComment_existing_thread_415()
			throws Exception {

		final int POI_UID = 415;

		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(3, getFileCount(dataDir_comments));
		assertEquals(2, commentManager.sizeOfCommentsByPoiId(POI_UID));

		final CommentBuilder comment = comments.create();

		final DateTime now = new DateTime();

		comment.setMessage("Hello World!");
		comment.setPostedAt(now);
		comment.setPostedBy("Toto");

		commentManager.addToCommentThreadByPoiId(POI_UID, comment);

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(4, getFileCount(dataDir_comments));

		comments.reload();

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(3, commentManager.sizeOfCommentsByPoiId(POI_UID));
	}

	private int getDbRowCount(final String tablename) throws SQLException {

		return executeDbQueryInt("SELECT COUNT(1) FROM " + tablename);
	}

	private int executeDbQueryInt(final String query) throws SQLException {

		final Statement stmt = cxn.createStatement();
		try {
			final ResultSet rs = stmt.executeQuery(query);
			try {

				rs.next();

				return rs.getInt(1);

			} finally {
				rs.close();
			}
		} finally {
			stmt.close();
		}
	}

	@Test
	public void testCommentManager_createComment_existing_thread_415_db()
			throws Exception {

		final int POI_UID = 415;

		assertEquals(0, searchHelper.search(CommentRef.class, "Hello").length);

		assertEquals(3, getDbRowCount("unm_entities_comments"));
		assertEquals(138, getDbRowCount("unm_searchtokens"));
		assertEquals(178, getDbRowCount("unm_search"));
		assertEquals(3, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(3, getFileCount(dataDir_comments));
		assertEquals(2, commentManager.sizeOfCommentsByPoiId(POI_UID));

		final CommentBuilder comment = comments.create();

		final DateTime now = new DateTime();

		comment.setMessage("Hello World!");
		comment.setPostedAt(now);
		comment.setPostedBy("Toto");

		commentManager.addToCommentThreadByPoiId(POI_UID, comment);

		assertEquals(1, searchHelper.search(CommentRef.class, "Hello").length);

		assertEquals(4, getDbRowCount("unm_entities_comments"));
		assertEquals(141, getDbRowCount("unm_searchtokens"));
		assertEquals(181, getDbRowCount("unm_search"));
		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(4, getFileCount(dataDir_comments));

		comments.reload();

		assertEquals(4, comments.getAllBy(Integer.class, "uid").size());
		assertEquals(2, commentManager.sizeOfThreads());
		assertEquals(3, commentManager.sizeOfCommentsByPoiId(POI_UID));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_createComment_existing_thread_415_noDate()
			throws Exception {

		final int POI_UID = 415;

		final CommentBuilder comment = comments.create();

		comment.setMessage("Hello World!");

		commentManager.addToCommentThreadByPoiId(POI_UID, comment);
	}

	@Test
	public void testGetComments_1_noComment() throws Exception {

		final int POI_UID = 1;

		assertTrue(commentManager.isNullByPoiId(POI_UID));
	}

	@Test
	public void testGetComments_poi415() throws Exception {

		final int POI_UID = 415;

		assertNotNull(pois.getByUid(POI_UID));

		assertFalse(commentManager.isNullByPoiId(POI_UID));

		final CommentThread commentThread = commentManager.getByPoiId(POI_UID);

		assertEquals(2, commentThread.sizeOfAllComments());
	}

	@Test
	public void testGetComment_1() throws Exception {

		final Comment comment = comments.getByUid(1);

		assertEquals(new DateTime(2014, 8, 24, 9, 50, 0), comment.getPostedAt());

		assertEquals("comments", comment.getCategory());
	}

	@Test
	public void testSearchComment_1() throws Exception {

		// 1: J’aime bien l’application, mais si on danse ?
		// 2: Une bien belle application.

		final CommentRef[] commentRefsA = searchHelper.search(CommentRef.class,
				"application");

		assertEquals(2, commentRefsA.length);
		assertEquals(1, commentRefsA[0].getUid());
		assertEquals(2, commentRefsA[1].getUid());

		final CommentRef[] commentRefsB = searchHelper.search(CommentRef.class,
				"aime danse");

		assertEquals(1, commentRefsB.length);
		assertEquals(1, commentRefsB[0].getUid());
	}

	@Test
	public void testSearchComment_0() throws Exception {

		final CommentRef[] commentRefs = searchHelper.search(CommentRef.class,
				"inexistent");

		assertEquals(0, commentRefs.length);
	}

	@Test
	public void testSearchComment_s_0() throws Exception {

		final CommentRef[] commentRefs = searchHelper.search(CommentRef.class,
				"s");

		assertEquals(0, commentRefs.length);
	}

	@Test
	public void testSearchComment_empty() throws Exception {

		assertEquals(3, comments.getAllByInt("uid").size());
		
		final CommentRef[] commentRefs = searchHelper.search(CommentRef.class,
				"");

		assertEquals(3, commentRefs.length);
	}
}
