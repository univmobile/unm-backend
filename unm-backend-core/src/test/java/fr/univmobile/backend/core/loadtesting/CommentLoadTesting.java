package fr.univmobile.backend.core.loadtesting;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.CommentBuilder;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class CommentLoadTesting extends AbstractLoadTesting {

	public static void main(final String... args) throws Exception {

		int count = 10000;

		String taskName = "taskCreateComment_new_threads";
		// "taskCreateComment_existing_thread_415"

		if (args != null) {

			if (args.length >= 1) {

				count = Integer.parseInt(args[0]);
			}

			if (args.length >= 2) {

				taskName = args[1];
			}
		}

		final CommentLoadTesting testing = new CommentLoadTesting();

		final Task task = (Task) testing.getClass().getDeclaredMethod(taskName)
				.invoke(testing);

		final File reportFile = new File("target", taskName + ".csv");

		testing.launch(count, task, reportFile);
	}

	private CommentLoadTesting() throws Exception {

		final File originalDataDir_comments = new File(
				"src/test/data/comments/001");

		final File dataDir_comments = new File(
				"target/CommentLoadTesting_comments");

		if (dataDir_comments.isDirectory()) {
			FileUtils.forceDelete(dataDir_comments);
		}

		FileUtils.copyDirectory(originalDataDir_comments, dataDir_comments);

		comments = BackendDataSourceFileSystem.newDataSource(
				CommentDataSource.class, dataDir_comments);

		final File originalDataDir_comment_threads = new File(
				"src/test/data/comment_threads/001");

		final File dataDir_comment_threads = new File(
				"target/CommentLoadTesting_comment_threads");

		if (dataDir_comment_threads.isDirectory()) {
			FileUtils.forceDelete(dataDir_comment_threads);
		}

		FileUtils.copyDirectory(originalDataDir_comment_threads,
				dataDir_comment_threads);

		throw new NotImplementedException();
		
//		commentThreads = BackendDataSourceFileSystem.newDataSource(
	//			CommentThreadDataSource.class, dataDir_comment_threads);

		//commentManager = new CommentManagerImpl(comments, commentThreads);
	}

	private final CommentDataSource comments;
	private final CommentThreadDataSource commentThreads;
	private final CommentManager commentManager;

	@SuppressWarnings("unused")
	private Task taskCreateComment_new_threads() {
		return new Task() {

			@Override
			public String getName() {

				return "taskCreateComment_new_threads";
			}

			@Override
			public void doTask(final int i) throws Exception {

				final int poi_uid = 100001 + i;

				final CommentBuilder comment = comments.create();

				comment.setMessage("Hello World!");
				comment.setPostedAt(new DateTime());
				comment.setPostedBy("Dummy");

				commentManager.addToCommentThreadByPoiId(poi_uid, comment);
			}
		};
	}

	@SuppressWarnings("unused")
	private Task taskCreateComment_existing_thread_415() {
		return new Task() {

			@Override
			public String getName() {

				return "taskCreateComment_existing_thread_415";
			}

			private static final int POI_UID = 415;

			@Override
			public void doTask(final int i) throws Exception {

				final CommentBuilder comment = comments.create();

				comment.setMessage("Hello World!");
				comment.setPostedAt(new DateTime());
				comment.setPostedBy("Dummy");

				commentManager.addToCommentThreadByPoiId(POI_UID, comment);
			}
		};
	}
}
