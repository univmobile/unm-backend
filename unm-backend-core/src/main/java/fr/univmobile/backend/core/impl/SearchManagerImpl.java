package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import fr.univmobile.backend.core.SearchEntry;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.search.Matchers;

public class SearchManagerImpl extends AbstractDbManagerImpl implements
		SearchManager {

	public SearchManagerImpl(final ConnectionType dbType, final Connection cxn)
			throws IOException {

		super(dbType, cxn);
	}

	public SearchManagerImpl(final ConnectionType dbType, final DataSource ds)
			throws IOException {

		super(dbType, ds);
	}

	@Override
	public void inject(final SearchEntry entry) throws IOException,
			SQLException {

		checkNotNull(entry, "entry");

		for (final Map.Entry<String, String> e : entry.fields()) {

			final String name = e.getKey();
			final String value = e.getValue();

			injectSearchTokens(entry.category, entry.entityId, //
					calcFieldNum(entry.category, name), value);
		}
	}

	private static int calcFieldNum(final String category, final String name) {

		if ("comments".equals(category)) {
			if ("postedBy".equals(name)) {
				return 1;
			} else if ("message".equals(name)) {
				return 2;
			}
		} else if ("pois".equals(category)) {
			if ("name".equals(name)) {
				return 1;
			} else if ("description".equals(name)) {
				return 2;
			} else if ("address".equals(name)) {
				return 3;
			} else if ("floor".equals(name)) {
				return 4;
			} else if ("openingHours".equals(name)) {
				return 5;
			} else if ("itinerary".equals(name)) {
				return 6;
			}
		}

		throw new IllegalArgumentException("category: " + category + ", name: "
				+ name);
	}

	private void injectSearchTokens(final String category, final int entityId,
			final int fieldNum, @Nullable final String text)
			throws SQLException {

		if (isBlank(text)) {
			return;
		}

		final String[] tokens = Matchers.tokenize(text);

		for (String token : tokens) {

			if (isBlank(token)) {
				throw new RuntimeException("text has blank token: " + text);
			}

			if (token.length() > 40) {
				token = token.substring(0, 40);
			}

			int tokenId = -1;

			if (!searchTokenExists(token)) {

				try {

					String normToken = Matchers.normalize(locale, token);

					if (normToken.length() > 40) {
						normToken = normToken.substring(0, 40);
					}

					tokenId = executeUpdateGetAutoIncrement(
							"createpkSearchToken", token, normToken);

				} catch (final SQLException e) {

					tokenId = -1; // Primary key violation: Token already exists
				}
			}

			if (tokenId == -1) {

				tokenId = executeQueryGetInt("getSearchTokenId", token);
			}

			executeUpdate("addSearchToken", tokenId, category, entityId,
					fieldNum);
		}
	}

	private final Locale locale = Locale.FRENCH;

	private final Set<String> cachedSearchTokens = new HashSet<String>();

	private boolean searchTokenExists(final String token) throws SQLException {

		if (cachedSearchTokens.contains(token)) {
			return true;
		}

		final Connection cxn = getConnection();
		try {
			final PreparedStatement pstmt = cxn
					.prepareStatement(getSql("getSearchTokenId"));
			try {

				pstmt.setString(1, token);

				final ResultSet rs = pstmt.executeQuery();
				try {

					if (rs.next()) {

						cachedSearchTokens.add(token);

						return true;

					} else {

						return false;
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
	}
}
