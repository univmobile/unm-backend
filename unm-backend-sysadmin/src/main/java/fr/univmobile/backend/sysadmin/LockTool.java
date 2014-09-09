package fr.univmobile.backend.sysadmin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import fr.univmobile.backend.core.impl.ConnectionType;
import fr.univmobile.backend.sysadmin.dao.Category;

/**
 * Code for the "lock" command-line tool.
 */
class LockTool extends AbstractTool {

	public LockTool(final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		super(dbType, cxn);
	}

	@Override
	public Result run() throws IOException, SQLException, SAXException {

		final List<Category> lockedCategories = new ArrayList<Category>();

		final Connection cxn = getConnection();
		try {
			final PreparedStatement pstmt = cxn
					.prepareStatement(getSql("getLockedCategories"));
			try {
				final ResultSet rs = pstmt.executeQuery();
				try {

					while (rs.next()) {

						final String id = rs.getString(1);
						final String path = rs.getString(2);
						final Timestamp lockedSince = rs.getTimestamp(3);

						lockedCategories.add(new Category(id, path,
								new DateTime(lockedSince)));
					}
				} finally {
					rs.close();
				}
			} finally {
				pstmt.close();
			}
		} finally {
			cxn.close();
		}

		System.out.println("Locked categories: " + lockedCategories.size());

		for (final Category lockedCategory : lockedCategories) {

			System.out.println( //
					"  " + lockedCategory.id + ", since: "
							+ lockedCategory.lockedSince + " -- "
							+ lockedCategory.path);

		}

		return new Result(lockedCategories.size());
	}
}
