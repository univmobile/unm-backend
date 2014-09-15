package fr.univmobile.backend.sysadmin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.univmobile.backend.core.Indexation;
import fr.univmobile.backend.core.IndexationObserver;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.core.impl.ConnectionType;
import fr.univmobile.backend.core.impl.IndexationImpl;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.history.LogQueue;

/**
 * Code for the "index" command-line tool.
 */
class IndexationTool extends AbstractTool {

	public IndexationTool(final File dataDir, final ConnectionType dbType,
			final Connection cxn) throws IOException,
			ParserConfigurationException {

		super(dbType, cxn);

		checkNotNull(dataDir, "dataDir");

		final LogQueue logQueue = new LogQueueDbImpl(dbType, cxn);

		final SearchManager searchManager = new SearchManagerImpl(logQueue,
				dbType, cxn);

		indexation = new IndexationImpl(new File(dataDir, "users"), //
				new File(dataDir, "regions"), //
				new File(dataDir, "pois"), //
				new File(dataDir, "comments"), //
				searchManager, dbType, cxn);
	}

	private final Indexation indexation;

	@Override
	public Result run() throws IOException, SQLException, SAXException {

		final Map<String, Integer> counts = new HashMap<String, Integer>();

		indexation.indexData(new IndexationObserver() {

			@Override
			public void notifyCategorySize(String category, int count) {

				System.out.println("  " + category + ": " + count);

				counts.put(category, count);
			}
		});

		int total = 0;

		for (final int count : counts.values()) {

			total += count;
		}

		return new Result(total);
	}
}
