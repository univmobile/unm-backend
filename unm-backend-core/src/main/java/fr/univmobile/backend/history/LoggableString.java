package fr.univmobile.backend.history;

/**
 * Simple implementation for {@link Loggable}, that takes as parameter a
 * formatted string.
 */
public final class LoggableString extends Loggable {

	public LoggableString(final String format, final Object... args) {

		this.message = String.format(format, args);
	}

	private final String message;

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = 3999195290843579448L;

	@Override
	public String getMessage(final int maxLength) {

		return message;
	}
}
