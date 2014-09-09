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

/**
 * Code for the "comment" command-line tool.
 */
class CommentTool extends AbstractTool {

	public CommentTool(final int limit, final ConnectionType dbType,
			final Connection cxn) throws IOException,
			ParserConfigurationException {

		super(dbType, cxn);

		this.limit = limit;
	}

	private final int limit;

	@Override
	public void run() throws IOException, SQLException, SAXException {

		final File commentsDir = getCategoryDir("comments");

		final PreparedStatement pstmt = cxn
				.prepareStatement(getSql("getComments"));
		try {
			pstmt.setInt(1, limit); // LIMIT

			int count = 0;

			final ResultSet rs = pstmt.executeQuery();
			try {

				while (rs.next()) {

					if (count >= limit) {
						System.out.println("...");
						break;
					}

					++count;

					final String path = rs.getString(1);

					final Comment comment = loadEntity(new File(commentsDir,
							path), Comment.class);

					final int uid = comment.getUid();
					final int poiUid = comment.getMainContext().getUid();

					System.out.println("#" + count + ": poi=" + poiUid
							+ " uid=" + uid + " " + comment.getPostedAt()
							+ " - " + comment.getPostedBy() + " - "
							+ comment.getMessage());
				}
			} finally {
				rs.close();
			}
		} finally {
			pstmt.close();
		}
	}
}
