package fr.univmobile.backend.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class Comments001Test {

	private static final File originalDataDir = new File(
			"src/main/resources/data/001/comments");

	@Before
	public void setUp() throws Exception {

		final File tmpDataDir = new File("target/Comments001Test");

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		comments = BackendDataSourceFileSystem.newDataSource(
				CommentDataSource.class, tmpDataDir);
	}

	private CommentDataSource comments;

	@Test
	public void test_count() throws Exception {

		assertEquals(3, comments.getAllByInt("uid").size());
	}

	@Test
	public void test_uniqueIds_entities() throws Exception {

		final Set<String> ids = new HashSet<String>();

		for (final Comment comment : comments.getAllByInt("uid").values()) {

			final String id = comment.getId();

			assertFalse("Duplicate id: " + id, ids.contains(id));

			ids.add(id);
		}

		assertEquals(3, ids.size());
	}

	@Test
	public void test_uniqueIds_revfiles() throws Exception {

		final Set<String> ids = new HashSet<String>();

		for (final File file : originalDataDir.listFiles()) {

			final Comment comment = DomBinderUtils.xmlContentToJava(file,
					Comment.class);

			final String id = comment.getId();

			assertFalse("Duplicate id: " + id, ids.contains(id));

			ids.add(id);
		}

		assertEquals(3, ids.size());
	}

	@Test
	public void test_uniqueUids() throws Exception {

		final Set<Integer> uids = new HashSet<Integer>();

		for (final Comment comment : comments.getAllByInt("uid").values()) {

			final int uid = comment.getUid();

			assertFalse("Duplicate uid: " + uid, uids.contains(uid));

			uids.add(uid);
		}

		assertEquals(3, uids.size());
	}
}
