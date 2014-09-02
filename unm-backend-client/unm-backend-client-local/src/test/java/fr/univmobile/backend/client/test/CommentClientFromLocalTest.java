package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Comment;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class CommentClientFromLocalTest {

	@Before
	public void setUp() throws Exception {

		final File tmpDataDir_comments = copyDirectory(new File(
				"src/test/data/comments/001"), new File(
				"target/CommentClientFromLocalTest_comments"));

		final CommentDataSource comments = BackendDataSourceFileSystem
				.newDataSource(CommentDataSource.class, tmpDataDir_comments);

		final CommentThreadDataSource commentThreads = BackendDataSourceFileSystem
				.newDataSource(
						CommentThreadDataSource.class,
						copyDirectory(
								new File("src/test/data/comment_threads/001"),
								new File(
										"target/CommentClientFromLocalTest_comment_threads")));

		client = new CommentClientFromLocal("http://dummy/", comments,
				commentThreads);
	}

	private CommentClient client;

	@Test
	public void test_getComments_poi415() throws IOException {

		final Comment[] comments = client.getCommentsByPoiId(415);

		assertEquals(3, comments.length);

		final Comment comment = comments[1];

		assertEquals("2", comment.getId());
		assertEquals("dandriana", comment.getAuthorUsername());
		assertNull(comment.getAuthorLang());

		assertEquals("Une bien belle application.", comment.getText());
	}

	@Test
	public void test_getEmptyComments_poi1() throws IOException {

		final Comment[] comments = client.getCommentsByPoiId(1);

		assertEquals(0, comments.length);
	}

	@Test
	public void test_getComment_1() throws IOException {

		final Comment comment = client.getCommentsByPoiId(415)[0];

		assertEquals("1", comment.getId());

		assertEquals(new DateTime(2014, 8, 24, 9, 50, 0), comment.getPostedAt());

		assertEquals("24 août — 9 h 50", comment.getDisplayPostedAt());

		assertEquals("Dimanche 24 août 2014, 9 h 50",
				comment.getDisplayFullPostedAt());

		assertEquals("9 h 50", comment.getDisplayPostedAtTime());
	}
}
