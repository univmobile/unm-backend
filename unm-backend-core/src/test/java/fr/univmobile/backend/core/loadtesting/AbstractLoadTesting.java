package fr.univmobile.backend.core.loadtesting;

public abstract class AbstractLoadTesting {

	protected AbstractLoadTesting() {

		System.out.println("Class: " + this.getClass().getName());

		System.out.println("Initializing...");
	}

	protected interface Task {

		void doTask(int index) throws Exception;
	}

	protected final void launch(final int count, final Task task)
			throws Exception {

		System.out.println("Launching...");

		final long startTime = System.currentTimeMillis();

		final long interval = 1000;

		long nextTime = startTime + interval;

		for (int i = 1; i <= count; ++i) {

			final long now = System.currentTimeMillis();

			if (now > nextTime) {

				final long elapsed = System.currentTimeMillis() - startTime;

				System.out.println(formatMs(elapsed) + " -- "
						+ ((100 * i) / count) + "% #" + i + "/" + count
						+ " -- avg " + ((elapsed * 1000) / i)
						+ "ms/1000 -- ETA "
						+ formatMs(((count - i) * elapsed) / i));

				nextTime = now + interval;
			}

			task.doTask(i);
		}

		final long elapsed = System.currentTimeMillis() - startTime;

		System.out.println(formatMs(elapsed) + " -- 100% #" + count + "/"
				+ count + " -- avg " + ((elapsed * 1000) / count)
				+ "ms/1000 -- Done.");
	}

	static String formatMs(final long ms) {

		final long sec = (ms + 999) / 1000;

		if (sec <= 60) {

			return sec + " sec";
		}

		return (sec / 60) + " min " + (sec % 60) + " sec";
	}
}
