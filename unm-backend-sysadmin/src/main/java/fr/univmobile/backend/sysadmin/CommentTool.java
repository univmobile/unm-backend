package fr.univmobile.backend.sysadmin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.backend.core.impl.CommentThreadDataSourceImpl;
import fr.univmobile.backend.core.impl.ConnectionType;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

/**
 * Code for the "comment" command-line tool.
 */
class CommentTool extends AbstractTool {

	public CommentTool(final int limit, final ConnectionType dbType,
			final Connection cxn) throws IOException, SQLException,
			ParserConfigurationException {

		super(dbType, cxn);

		this.limit = limit;

		final CommentDataSource comments = BackendDataSourceFileSystem
				.newDataSource(CommentDataSource.class,
						getCategoryDir("comments"));

//		final CommentThreadDataSource commentThreads = new CommentThreadDataSourceImpl(
	//			dbType, cxn);

		commentManager = new CommentManagerImpl(comments, dbType,cxn);
	}

	private final int limit;

	// private final CommentThreadDataSource commentThreads;

	private final CommentManager commentManager;

	@Override
	public CommentResult run() throws IOException, SQLException, SAXException {

		final CommentResult result = new CommentResult();

		int count = 0;
		
		for (final Comment comment:
			commentManager.getMostRecentComments(limit+1)) {
			
			if (count >= limit) {
				System.out.println("...");
				break;
			}
			
			++count;	
			
			final int uid = comment.getUid();
			final int poiUid = comment.getMainContext().getUid();

			result.addComment(comment);

			System.out.println("#" + count + ": poi=" + poiUid
					+ " uid=" + uid + " " + comment.getPostedAt()
					+ " - " + comment.getPostedBy() + " - "
					+ comment.getMessage());
		}
		
		return result;

		/*
		final File commentsDir = getCategoryDir("comments");

		final CommentResult result = new CommentResult();

		*/
	}
}
