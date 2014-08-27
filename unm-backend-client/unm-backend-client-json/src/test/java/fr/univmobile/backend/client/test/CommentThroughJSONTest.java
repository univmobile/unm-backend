package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Comment;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromJSON;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.client.json.CommentJSONClientImpl;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class CommentThroughJSONTest {

	@Before
	public void setUp() throws Exception {

		final CommentDataSource commentDataSource = BackendDataSourceFileSystem
				.newDataSource(
						CommentDataSource.class,
						copyDirectory(new File("src/test/data/comments/001"),
								new File("target/CommentThroughJSONTest_comments")));

		final CommentThreadDataSource commentThreadDataSource = BackendDataSourceFileSystem
				.newDataSource(
						CommentThreadDataSource.class,
						copyDirectory(new File("src/test/data/comment_threads/001"),
								new File("target/CommentThroughJSONTest_comment_threads")));

		final CommentClientFromLocal commentClient = new CommentClientFromLocal(
				commentDataSource, commentThreadDataSource);

		commentJSONClient = new CommentJSONClientImpl("(dummy baseURL)", commentClient);

		client = new CommentClientFromJSON(commentJSONClient);
	}

	private CommentJSONClientImpl commentJSONClient;
	private CommentClient client;

	@Test
	public void testThroughJSON_emptyComments_poi1() throws IOException {

		final Comment[] comments = client.getCommentsByPoiId(1);

		assertEquals(0, comments.length);
	}

	@Test
	public void testThroughJSON_comments_poi415() throws IOException {

		final Comment[] comments = client.getCommentsByPoiId(415);

		assertEquals(3, comments.length);
		
		final Comment comment = comments[1];

		assertEquals("2", comment.getId());
		assertEquals("dandriana", comment.getAuthorUsername());
		assertTrue(comment.isNullAuthorLang());
		assertNull(comment.getAuthorLang());

		assertEquals("Une bien belle application.", comment.getText());
	}
}
