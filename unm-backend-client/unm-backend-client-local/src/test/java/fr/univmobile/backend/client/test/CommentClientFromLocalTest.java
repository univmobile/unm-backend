package fr.univmobile.backend.client.test;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Comment;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.Indexation;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.backend.core.impl.IndexationImpl;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class CommentClientFromLocalTest {

	@Before
	public void setUp() throws Exception {

		final File tmpDataDir_comments = copyDirectory(new File(
				"src/test/data/comments/001"), new File(
				"target/CommentClientFromLocalTest_comments"));

		final CommentDataSource comments = BackendDataSourceFileSystem
				.newDataSource(CommentDataSource.class, tmpDataDir_comments);

		final File dbFile = new File("target/CommentClientFromLocalTest.h2.db");

		FileUtils.deleteQuietly(dbFile);

		assertFalse(dbFile.exists());

		final String url = "jdbc:h2:./target/CommentClientFromLocalTest";

		cxn = DriverManager.getConnection(url);

		final LogQueue logQueue = new LogQueueDbImpl(H2, cxn);

		final SearchManagerImpl searchManager = new SearchManagerImpl(logQueue,
				H2, cxn);

		final Indexation indexation = new IndexationImpl(//
				new File("src/test/data/users/001"), //
				new File("src/test/data/regions/001"), //
				new File("src/test/data/pois/001"), //
				new File("target/CommentClientFromLocalTest_comments"), //
				searchManager, H2, cxn);

		indexation.indexData(null);

		final CommentManager commentManager = new CommentManagerImpl(logQueue,
				comments, searchManager, H2, cxn);

		client = new CommentClientFromLocal("http://dummy/", comments,
				commentManager, searchManager);
		
		LogQueueDbImpl.setPrincipal("crezvani");
	}

	private CommentClient client;
	private Connection cxn;

	@After
	public void tearDown() throws Exception {

		if (cxn != null) {

			cxn.close();

			cxn = null;
		}
	}

	@Test
	public void test_getComments_poi4398() throws Exception {

		final Comment[] comments = client.getCommentsByPoiId(4398);

		assertEquals(3, comments.length);

		final Comment comment = comments[1];

		assertEquals("2", comment.getId());
		assertEquals("dandriana", comment.getAuthorUsername());
		assertNull(comment.getAuthorLang());

		assertEquals("Une bien belle application.", comment.getText());
	}

	@Test
	public void test_getMostRecentComments() throws Exception {

		final Comment[] comments = client.getMostRecentComments(100);

		assertEquals(3, comments.length);

		final Comment comment = comments[1];

		assertEquals("2", comment.getId());
		assertEquals("dandriana", comment.getAuthorUsername());
		assertNull(comment.getAuthorLang());

		assertEquals("Une bien belle application.", comment.getText());
	}

	@Test
	public void test_getSearchComments() throws Exception {

		final Comment[] comments = client.searchComments("application", 100);

		assertEquals(2, comments.length);

		final Comment comment = comments[1];

		assertEquals("2", comment.getId());
		assertEquals("dandriana", comment.getAuthorUsername());
		assertNull(comment.getAuthorLang());

		assertEquals("Une bien belle application.", comment.getText());
	}

	@Test
	public void test_getEmptyComments_poi1() throws Exception {

		final Comment[] comments = client.getCommentsByPoiId(1);

		assertEquals(0, comments.length);
	}

	@Test
	public void test_getComment_1() throws Exception {

		final Comment comment = client.getCommentsByPoiId(4398)[2];

		assertEquals("1", comment.getId());

		assertEquals(new DateTime(2014, 8, 24, 9, 50, 0), comment.getPostedAt());

		assertEquals("24 août — 9 h 50", comment.getDisplayPostedAt());

		assertEquals("Dimanche 24 août 2014, 9 h 50",
				comment.getDisplayFullPostedAt());

		assertEquals("9 h 50", comment.getDisplayPostedAtTime());
	}
}
