package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import com.avcompris.lang.NotImplementedException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.CommentThread.CommentRef;
import fr.univmobile.backend.core.EntryRef;
import fr.univmobile.backend.core.SearchEntry;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.search.Matchers;
import fr.univmobile.backend.search.SearchContext;
import fr.univmobile.backend.search.SearchQuery;
import fr.univmobile.backend.search.SearchQueryText;

public class SearchManagerImpl extends AbstractDbManagerImpl implements
		SearchManager {

	public SearchManagerImpl(final ConnectionType dbType, final Connection cxn)
			throws IOException {

		super(dbType, cxn);

		this.cxn = cxn;
		this.ds = null;
	}

	public SearchManagerImpl(final ConnectionType dbType, final DataSource ds)
			throws IOException {

		super(dbType, ds);

		this.cxn = null;
		this.ds = ds;
	}

	@Nullable
	private final Connection cxn;

	@Nullable
	private final DataSource ds;

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

	private final LoadingCache<String, Integer> searchTokenIds = CacheBuilder
			.newBuilder().build(new CacheLoader<String, Integer>() {

				@Override
				public Integer load(final String token) throws SQLException {

					return executeQueryGetInt("getSearchTokenId", token);
				}
			});

	private int getSearchTokenId(final String token) throws SQLException {

		try {

			return searchTokenIds.get(token);

		} catch (final ExecutionException e) {

			throw new SQLException(e.getCause());
		}
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

					searchTokensExist.put(token, true);

				} catch (final SQLException e) {

					tokenId = -1; // Primary key violation: Token already exists
				}

				searchTokenExists(token);
			}

			if (tokenId == -1) {

				tokenId = getSearchTokenId(token);
			}

			executeUpdate("addSearchToken", tokenId, category, entityId,
					fieldNum);
		}
	}

	private final Locale locale = Locale.FRENCH;

	private final Cache<String, Boolean> searchTokensExist = CacheBuilder
			.newBuilder().build();

	private boolean searchTokenExists(final String token) throws SQLException {

		final Boolean cached = searchTokensExist.getIfPresent(token);

		if (cached != null && cached) {
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

						searchTokensExist.put(token, true);

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

	@Override
	public EntryRef[] search(final SearchContext context,
			final SearchQuery query) throws IOException, SQLException {

		checkNotNull(context, "context");

		if (!SearchContextImpl.class.equals(context.getClass())) {
			throw new NotImplementedException("context.class: "
					+ context.getClass());
		}

		final Class<? extends EntryRef> clazz = ((SearchContextImpl) context).clazz;

		if (clazz == null) {
			throw new NotImplementedException("clazz == null");
		}

		final String queryId;

		if (CommentRef.class.equals(clazz)) {
			queryId = "searchCommentUidsByTokenIds";
		} else {
			throw new NotImplementedException("clazz: " + clazz);
		}

		if (!SearchQueryText.class.isInstance(query)) {
			throw new NotImplementedException("query.class: "
					+ query.getClass());
		}

		final String[] tokens = ((SearchQueryText) query).getTextItems();

		final int[] tokenIds = getTokenIds(tokens);

		final String sql = setIntArray(getSql(queryId), 1, tokenIds);

		final List<CommentRef> commentRefs = new ArrayList<CommentRef>();

		final Connection cxn = getConnection();
		try {
			final PreparedStatement pstmt = cxn.prepareStatement(sql);
			try {

				// final Array array = cxn.createArrayOf("BIGINT", tokenIds);

				final ResultSet rs = pstmt.executeQuery();
				try {

					// pstmt.setArray(1, array);

					while (rs.next()) {

						final int uid = rs.getInt(1);

						final CommentRef commentRef = new CommentRef() {

							@Override
							public String getEntryRefId() {

								return Integer.toString(uid);
							}

							@Override
							public String getCategory() {

								return "comments";
							}

							@Override
							public int getUid() {

								return uid;
							}
						};

						commentRefs.add(commentRef);
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

		return Iterables.toArray(commentRefs, CommentRef.class);
	}

	private int[] getTokenIds(final String[] tokens) throws IOException,
			SQLException {

		final List<Integer> tokenIds = new ArrayList<Integer>();

		for (final String token : tokens) {

			if (!searchTokenExists(token)) {
				continue;
			}

			final int tokenId = getSearchTokenId(token);

			tokenIds.add(tokenId);
		}

		final int[] array = new int[tokenIds.size()];

		int i = 0;

		for (final Integer tokenId : tokenIds) {

			array[i] = tokenId;

			++i;
		}

		return array;
	}

	@Override
	public SearchContext newSearchContext() throws IOException {

		return new SearchContextImpl(dbType, cxn);
	}

	private static class SearchContextImpl extends AbstractDbManagerImpl
			implements SearchContext {

		public SearchContextImpl(final ConnectionType dbType,
				final Connection cxn) throws IOException {

			super(dbType, cxn);
		}

		private Class<? extends EntryRef> clazz = null;

		@Override
		public int size() throws IOException, SQLException {

			if (clazz == null) {

				return 0;

			} else if (Comment.class.equals(clazz)) {

				return executeQueryGetInt("getCommentCount");

			} else {

				throw new IllegalStateException("Unknown clazz: "
						+ clazz.getName());
			}
		}

		@Override
		public void restrictTo(final Class<? extends EntryRef> clazz) {

			this.clazz = checkNotNull(clazz, "clazz");
		}
	}

}
