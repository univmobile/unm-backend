package fr.univmobile.backend.core.loadtesting;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public abstract class AbstractLoadTesting {

	protected AbstractLoadTesting() {

		System.out.println("Class: " + this.getClass().getName());

		System.out.println("Initializing...");
	}

	protected interface Task {

		String getName();

		void doTask(int index) throws Exception;
	}

	protected final void launch(final int count, final Task task,
			final File reportFile) throws Exception {

		System.out.println("Report File: " + reportFile.getCanonicalPath());

		System.out.println("Launching...");

		final OutputStream os = new FileOutputStream(reportFile);
		try {

			final PrintWriter pw = new PrintWriter(new OutputStreamWriter(os,
					UTF_8));
			try {

				run(count, task, pw);

			} finally {

				pw.flush();

				pw.close();
			}
			
		} finally {
			os.close();
		}
	}

	private void run(final int count, final Task task, final PrintWriter pw)
			throws Exception {

		pw.println("# Task: " + task.getName() + " -- count: " + count);
		pw.println("# Format: INDEX;TOTAL_ELAPSED_MS");
		pw.println();

		final long startTime = System.currentTimeMillis();

		final long interval = 5000;

		long nextTime = startTime + interval;

		for (int i = 1; i <= count; ++i) {

			task.doTask(i);

			final long now = System.currentTimeMillis();

			final long elapsed = now - startTime;

			pw.print(i);
			pw.print(';');
			pw.println(elapsed);

			if (now > nextTime) {

				System.out.println(formatMs(elapsed) + " -- "
						+ ((100 * i) / count) + "% #" + i + "/" + count
						+ " -- avg " + ((elapsed * 1000) / i) / 1000.0
						+ "sec/1000 -- ETA "
						+ formatMs(((count - i) * elapsed) / i));

				nextTime = now + interval;
			}
		}

		final long elapsed = System.currentTimeMillis() - startTime;

		final double avg = ((elapsed * 1000) / count) / 1000.0;

		pw.println();
		pw.println("# Total Time: " + formatMs(elapsed) + " -- avg " + avg
				+ "sec/1000");

		System.out.println(formatMs(elapsed) + " -- 100% #" + count + "/"
				+ count + " -- avg " + avg + "sec/1000 -- Done.");
	}

	static String formatMs(final long ms) {

		final long sec = (ms + 999) / 1000;

		if (sec <= 60) {

			return sec + " sec";
		}

		return (sec / 60) + " min " + (sec % 60) + " sec";
	}
}
