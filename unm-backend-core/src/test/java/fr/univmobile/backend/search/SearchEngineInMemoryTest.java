package fr.univmobile.backend.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.EntryRef;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class SearchEngineInMemoryTest {

	public SearchEngineInMemoryTest() throws Exception {

		comments = BackendDataSourceFileSystem
				.newDataSource(CommentDataSource.class, new File(
						"src/test/data/comments/001"));
	}

	@Before
	public void setUp() throws Exception {

		engine = new SearchHelper(new SearchEngineInMemory());
	}

	private SearchHelper engine;

	private final CommentDataSource comments;

	@Test
	public void testContext_addComments() throws Exception {

		final SearchContextInMemory context = new SearchContextInMemory();

		assertEquals(0, context.size());

		context.add(comments.getByUid(1));

		assertEquals(1, context.size());

		context.add(comments.getByUid(1));

		assertEquals(1, context.size());

	}

	@Test
	public void testSearch_empty() throws Exception {

		final SearchContext context = new SearchContextInMemory();

		final EntryRef[] result = engine.search(context, "application");

		assertEquals(0, result.length);
	}

	@Test
	public void testSearch_oneCommentMatches() throws Exception {

		// Dominique
		// J’aime bien l’application, mais si on danse ?
		final SearchContext context = new SearchContextInMemory(
				comments.getByUid(1));

		final EntryRef[] result = engine.search(context, "application");

		assertEquals(1, result.length);

		assertEquals("1", result[0].getEntryRefId());
	}

	@Test
	public void testSearch_oneCommentMatches_Unicode() throws Exception {

		// Dominique
		// Ceci est un troisième commentaire.
		final SearchContext context = new SearchContextInMemory(
				comments.getByUid(3));

		final EntryRef[] result = engine.search(context, "troisième");

		assertEquals(1, result.length);

		assertEquals("3", result[0].getEntryRefId());
	}

	@Test
	public void testSearch_oneCommentMatches_Lowercase() throws Exception {

		// Dominique
		// Ceci est un troisième commentaire.
		final SearchContext context = new SearchContextInMemory(
				comments.getByUid(3));

		assertEquals(1, engine.search(context, "ceci").length);
		assertEquals(1, engine.search(context, "Ceci").length);
		assertEquals(1, engine.search(context, "CECI").length);
	}

	@Test
	public void testSearch_oneCommentMatches_not() throws Exception {

		// Dominique
		// Ceci est un troisième commentaire.
		final SearchContext context = new SearchContextInMemory(
				comments.getByUid(3));

		final EntryRef[] result = engine.search(context, "orange");

		assertEquals(0, result.length);
	}

	@Test
	public void testSearch_matches() throws Exception {

		assertTrue(engine.match( "application", comments.getByUid(1)));
	}
}
