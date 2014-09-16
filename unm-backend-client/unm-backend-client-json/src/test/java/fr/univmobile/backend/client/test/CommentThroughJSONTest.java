package fr.univmobile.backend.client.test;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Comment;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromJSON;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.client.json.CommentJSONClientImpl;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.Indexation;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.backend.core.impl.IndexationImpl;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class CommentThroughJSONTest {

	@Before
	public void setUp() throws Exception {

		final File dbFile = new File("target/CommentThroughJSONTest.h2.db");

		FileUtils.deleteQuietly(dbFile);

		assertFalse(dbFile.exists());

		final String url = "jdbc:h2:./target/CommentThroughJSONTest";

		cxn = DriverManager.getConnection(url);

		final LogQueue logQueue = new LogQueueDbImpl(H2, cxn);

		final SearchManagerImpl searchManager = new SearchManagerImpl(logQueue,
				H2, cxn);

		final Indexation indexation = new IndexationImpl(//
				new File("src/test/data/users/001"), //
				new File("src/test/data/regions/001"), //
				new File("src/test/data/pois/001"), //
				new File("src/test/data/comments/001"), //
				searchManager, H2, cxn);

		indexation.indexData(null);

		final CommentDataSource commentDataSource = BackendDataSourceFileSystem
				.newDataSource(
						CommentDataSource.class,
						copyDirectory(
								new File("src/test/data/comments/001"),
								new File(
										"target/CommentThroughJSONTest_comments")));

		final CommentManager commentManager = new CommentManagerImpl(logQueue,
				commentDataSource, searchManager, H2, cxn);

		final CommentClientFromLocal commentClient = new CommentClientFromLocal(
				"(dummy baseURL)", commentDataSource, commentManager,
				searchManager);

		commentJSONClient = new CommentJSONClientImpl(commentClient);

		client = new CommentClientFromJSON(commentJSONClient);
		
		LogQueueDbImpl.setPrincipal("crezvani");
	}

	private CommentJSONClientImpl commentJSONClient;
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
	public void testThroughJSON_emptyComments_poi1() throws Exception {

		final Comment[] comments = client.getCommentsByPoiId(1);

		assertEquals(0, comments.length);
	}

	@Test
	public void testThroughJSON_comments_poi2714() throws Exception {

		final Comment[] comments = client.getCommentsByPoiId(2714);

		assertEquals(3, comments.length);

		final Comment comment = comments[1];

		assertEquals("2", comment.getId());
		assertEquals("dandriana", comment.getAuthorUsername());
		assertTrue(comment.isNullAuthorLang());
		assertNull(comment.getAuthorLang());

		assertEquals("Une bien belle application.", comment.getText());
	}

	@Test
	public void testThroughJSON_searchComments() throws Exception {

		final Comment[] comments = client.searchComments("belle", 100);

		assertEquals(1, comments.length);

		final Comment comment = comments[0];

		assertEquals("2", comment.getId());
		assertEquals("dandriana", comment.getAuthorUsername());
		assertTrue(comment.isNullAuthorLang());
		assertNull(comment.getAuthorLang());

		assertEquals("Une bien belle application.", comment.getText());
	}
}
