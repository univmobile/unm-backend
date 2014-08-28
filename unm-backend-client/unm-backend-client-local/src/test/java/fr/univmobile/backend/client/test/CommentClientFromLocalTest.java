package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

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

		client = new CommentClientFromLocal("http://dummy/", comments, commentThreads);
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
		/*
		 * <content uid="2" active="true" postedAt="2014-08-25 09:17:00"
		 * postedBy="André" title="Une belle application"> <context
		 * id="fr.univmobile:unm-backend:test/pois/001:poi415_1"
		 * type="local:poi" uid="415"/> <reference uid="1"/> <message> Une bien
		 * belle application. </message> </content> </entry>
		 */
	}

	@Test
	public void test_getEmptyComments_poi1() throws IOException {

		final Comment[] comments = client.getCommentsByPoiId(1);

		assertEquals(0, comments.length);
	}
}
