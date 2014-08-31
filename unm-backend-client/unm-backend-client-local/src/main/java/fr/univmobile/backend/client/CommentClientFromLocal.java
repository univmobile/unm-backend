package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentThread;
import fr.univmobile.backend.core.CommentThread.CommentRef;
import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.commons.DataBeans;

public class CommentClientFromLocal extends AbstractClientFromLocal implements
		CommentClient {

	@Inject
	public CommentClientFromLocal(final String baseURL,
			final CommentDataSource commentDataSource,
			final CommentThreadDataSource commentThreadDataSource) {

		super(baseURL);

		this.commentDataSource = checkNotNull(commentDataSource,
				"commentDataSource");

		this.commentThreadDataSource = checkNotNull(commentThreadDataSource,
				"commentThreadDataSource");
	}

	private final CommentDataSource commentDataSource;
	private final CommentThreadDataSource commentThreadDataSource;

	private static final Log log = LogFactory
			.getLog(CommentClientFromLocal.class);

	@Override
	public Comment[] getCommentsByPoiId(final int poiId) throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getCommentsByPoiId():" + poiId + "...");
		}

		if (commentThreadDataSource.isNullByPoiId(poiId)) {
			return new Comment[0];
		}

		final CommentThread commentThread = commentThreadDataSource
				.getByPoiId(poiId);

		final CommentRef[] commentRefs = commentThread.getAllComments();

		final Comment[] comments = new Comment[commentRefs.length];

		for (int i = 0; i < commentRefs.length; ++i) {

			final CommentRef commentRef = commentRefs[i];

			final int uid = commentRef.getUid();

			final fr.univmobile.backend.core.Comment dsComment = commentDataSource
					.getByUid(uid);

			final DateTime postedAt = dsComment.getPostedAt();

			final MutableComment comment = DataBeans //
					.instantiate(MutableComment.class) //
					.setId(Integer.toString(uid)) //
					.setAuthorUsername(dsComment.getAuthorName()) //
					.setAuthorDisplayName(dsComment.getPostedBy()) //
					.setText(dsComment.getMessage()) //
					.setPostedAt(postedAt) //
					.setDisplayFullPostedAt(formatDateFull(postedAt)) //
					.setDisplayPostedAt(formatDate(postedAt)) //
					.setDisplayPostedAtTime(formatTime(postedAt)); //

			comments[i] = comment;
		}

		return comments;
	}

	private interface MutableComment extends Comment {

		MutableComment setId(String id);

		MutableComment setUrl(String url);

		MutableComment setText(String text);

		MutableComment setPostedAt(DateTime date);

		MutableComment setDisplayPostedAt(String date);

		MutableComment setDisplayFullPostedAt(String date);

		MutableComment setDisplayPostedAtTime(String date);

		MutableComment setAuthorUsername(@Nullable String address);

		MutableComment setAuthorDisplayName(@Nullable String phone);

		MutableComment setFloor(@Nullable String floor);
	}
}
