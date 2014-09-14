package fr.univmobile.backend;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.univmobile.commons.DataBeans;
import fr.univmobile.commons.FormatUtils;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "logs", "logs/" })
public class LogsController extends AbstractBackendController {

	public LogsController() {

	}

	@Override
	public View action() throws Exception {

		getDelegationUser();

		// 1. LOGS

		boolean desc = true; // Default

		int limit = 200; // Default

		final Logs httpInputs = getHttpInputs(Logs.class);

		if (httpInputs.isHttpValid()) {

			try {

				limit = Integer.parseInt(httpInputs.limit());

			} catch (final Exception e) {

				// do nothing
			}

			if (httpInputs.desc() == null && httpInputs.undesc() != null) {

				desc = false;
			}
		}

		final LogsInfo logsInfo = DataBeans.instantiate(LogsInfo.class) //
				.setDesc(desc) //
				.setLimit(limit); //

		setAttribute("logsInfo", logsInfo);

		final String logs;

		final String logFilePath = SystemController.loadLogFilePath();

		if (logFilePath == null) {
			throw new FileNotFoundException("Cannot load Log File");
		}

		final File logFile = new File(logFilePath);

		final String canonicalPath = logFile.getCanonicalPath();

		if (!logFile.isFile()) {
			throw new IOException("Cannot access Log File: " + canonicalPath);
		}

		logsInfo.setLogFile(canonicalPath).setLogFileLength(
				FormatUtils.formatFileLength(logFile.length()));

		final String[] lines = new String[limit];

		int offset = 0;
		int total = 0;

		final InputStream is = new FileInputStream(logFile);
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					is, UTF_8));
			try {
				while (true) {

					final String line = br.readLine();

					if (line == null) {
						break;
					}

					lines[offset] = line;

					++total;

					++offset;

					if (offset >= limit) {

						offset = 0;
					}
				}
			} finally {
				br.close();
			}
		} finally {
			is.close();
		}

		final int lineCount = Math.max(0, Math.min(total, limit));

		logsInfo.setLineCount(lineCount).setTotal(total);

		final StringBuilder sb = new StringBuilder();

		offset = desc ? (offset + limit - 1) : (offset + limit - lineCount);

		for (int i = 0; i < lineCount; ++i) {

			if (i != 0) {

				sb.append("\n");
			}

			sb.append(lines[(desc ? offset - i : offset + i) % limit]);
		}

		logs = sb.toString();

		setAttribute("logs", logs);

		// 9. END

		return new View("logs.jsp");
	}

	@HttpMethods("GET")
	private interface Logs extends HttpInputs {

		@HttpParameter("u")
		String undesc();

		@HttpParameter("desc")
		String desc();

		@HttpParameter("limit")
		String limit();
	}

	private interface LogsInfo {

		/**
		 * The number of lines fetched.
		 */
		int getLineCount();

		LogsInfo setLineCount(int lineCount);

		/**
		 * The total number of lines in the file.
		 */
		int getTotal();

		LogsInfo setTotal(int total);

		/**
		 * The path to the Log File.
		 */
		String getLogFile();

		LogsInfo setLogFile(String logFile);

		String getLogFileLength();

		LogsInfo setLogFileLength(String logFileLength);

		/**
		 * The maximum number of lines to fetch.
		 */
		int getLimit();

		LogsInfo setLimit(int limit);

		/**
		 * Do we want the results in desc order?
		 */
		boolean getDesc();

		LogsInfo setDesc(boolean desc);
	}
}
