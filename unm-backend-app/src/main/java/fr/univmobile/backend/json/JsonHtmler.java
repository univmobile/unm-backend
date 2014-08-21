package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.substringBetween;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

/**
 * A class that transform JSON streams into HTML fragments, with indentation and
 * links.
 */
public class JsonHtmler {

	public static String jsonToHtml(final String json) throws IOException {

		checkNotNull(json, "json");

		final StringBuilder sb = new StringBuilder("<pre>");

		new JsonHtmler(sb, json).parse();

		sb.append("</pre>");

		return sb.toString();
	}

	private JsonHtmler(final StringBuilder sb, final String json) {

		this.sb = checkNotNull(sb, "sb");
		this.json = checkNotNull(json, "json");

		length = json.length();
	}

	private final StringBuilder sb;
	private final String json;
	private final int length;
	private int offset;
	private int lineNumber = 1;
	private int colNumber = 1;

	private char lastChar;
	private Character nextChar = null;

	private char nextChar() throws IOException {

		if (nextChar != null) {

			final char c = nextChar;

			nextChar = null;

			lastChar = c;

			return c;
		}

		if (offset < length) {

			final char c = json.charAt(offset);

			++offset;

			++colNumber;

			switch (c) {
			case '\r':
				++lineNumber;
				colNumber = 1;
				break;
			case '\n':
				if (lastChar != '\n') {
					++lineNumber;
				}
				colNumber = 1;
				break;
			default:
				break;
			}

			lastChar = c;

			return c;
		}

		throw new EOFException("End of the JSON stream (length = " + length
				+ ")");
	}

	private char nextNonBlankChar() throws IOException {

		while (true) {

			final char c = nextChar();

			if (!Character.isWhitespace(c)) {

				return c;
			}
		}
	}

	private char nextNonBlankChar(final char c) throws IOException {

		final char x = nextNonBlankChar();

		if (x != c) {
			throw new IOException("Illegal char \"" + x + "\" at "
					+ getPosition() + ": Expected \"" + c + "\"");
		}

		return x;
	}

	private String nextString() throws IOException {

		final StringBuilder sb = new StringBuilder();

		while (true) {

			final char c = nextChar();

			switch (c) {

			case '\\':
				final char c2 = nextChar();
				sb.append(c2);
				break;

			case '"':
				return sb.toString();

			default:
				sb.append(c);
				break;
			}
		}
	}

	private String nextWord() throws IOException {

		final StringBuilder sb = new StringBuilder();

		loop: while (true) {

			final char c = nextChar();

			if (Character.isWhitespace(c)) {
				nextChar = c;
				break;
			}

			switch (c) {

			case '\\':
				throw new NotImplementedException("c=" + c);

			case ',':
				nextChar = c;
				break loop;

			default:
				sb.append(c);
				break;
			}
		}

		return sb.toString();
	}

	private void parse() throws IOException {

		final char c = nextNonBlankChar();

		if (c != '{') {
			throw new IllegalArgumentException(
					"JSON stream should start with \"{\"");
		}

		sb.append(parseMap(0));
	}

	private String parseMap(final int indent) throws IOException {

		final StringBuilder sb = new StringBuilder();

		sb.append('{');

		// Note: Do not use a Map, since we must keep the order.

		final List<Field> fields = new ArrayList<Field>();

		while (true) {

			final char n = nextNonBlankChar();

			switch (n) {

			case '}':
				final int count = fields.size();
				if (count == 0) {
					sb.append('}');
				} else {
					Collections.sort(fields);
					int maxKeyLength = 0;
					int scalarCount = 0;
					for (final Field field : fields) {
						if (field.isScalar) {
							++scalarCount;
							final int keyLength = field.key.length();
							if (keyLength > maxKeyLength) {
								maxKeyLength = keyLength;
							}
						}
					}
					if (scalarCount > 1) {
						++maxKeyLength; // Add one space when multiple scalars
					}
					// 1. SCALARS
					int lastIndexNonScalar = -1;
					for (int i = 0; i < count; ++i) {
						final Field field = fields.get(i);
						if (!field.isScalar) {
							lastIndexNonScalar = i;
							continue;
						}
						sb.append("\r\n").append(repeat(' ', 4 * (indent + 1)))
								.append('"').append(field.key).append("\":");
						if (field.isScalar) {
							sb.append(repeat(' ',
									maxKeyLength - field.key.length()));
						}
						if ("url".equals(field.key)) {
							if ("null".equals(field.value)) {
								sb.append("null");
							} else if (field.value.startsWith("\"")
									&& field.value.endsWith("\"")) {
								final String url = substringBetween(
										field.value, "\"");
								final String query = url.contains("?") ? "&html"
										: "?html";
								sb.append("\"<a href=\"" + url + query + "\">")
										.append(url).append("</a>\"");
							} else {
								throw new IOException("Illegal url value: "
										+ field.value);
							}
						} else {
							sb.append(field.value);
						}
						if (i + 1 < count || scalarCount < count) {
							sb.append(',');
						}
					}
					// 2. NON-SCALARS
					for (int i = 0; i < count; ++i) {
						final Field field = fields.get(i);
						if (field.isScalar) {
							continue;
						}
						sb.append("\r\n").append(repeat(' ', 4 * (indent + 1)))
								.append('"').append(field.key).append("\":");
						sb.append(field.value);
						if (i < lastIndexNonScalar) {
							sb.append(',');
						}
					}
					sb.append("\r\n").append(repeat(' ', 4 * indent))
							.append('}');
				}

				// sb.append(nextString());
				// sb.append("\":");
				return sb.toString();

			case '"':
				final String key = nextString();
				nextNonBlankChar(':');
				final String value;
				final boolean isScalar;
				final char c = nextNonBlankChar();
				switch (c) {
				case '"':
					isScalar = true;
					value = '"' + nextString() + '"';
					break;
				case '{':
					isScalar = false;
					value = parseMap(indent + 1);
					break;
				case '[':
					isScalar = false;
					value = parseArray(indent + 1);
					break;
				default:
					isScalar = true;
					value = c + nextWord();
					// throw new NotImplementedException("c=" + c + " at "
					// + getPosition());
				}
				fields.add(new Field(key, value, isScalar));
				break;

			case ',':
				break;

			default:
				throw new IOException("Illegal character \"" + n
						+ "\" in JSON stream at " + getPosition());
			}
		}
	}

	private String parseArray(final int indent) throws IOException {

		final StringBuilder sb = new StringBuilder();

		sb.append('[');

		for (int count = 0;; ++count) {

			final char n = nextNonBlankChar();

			switch (n) {

			case ']':
				if (count == 0) {
					sb.append(']');
				} else {
					sb.append("\r\n").append(repeat(' ', 4 * indent))
							.append(']');
				}

				return sb.toString();

			case '{':
				if (count == 0) {
					sb.append("\r\n").append(repeat(' ', 4 * (indent + 1)));
				}
				sb.append(parseMap(indent + 1));
				break;

			case ',':
				sb.append(",\r\n").append(repeat(' ', 4 * (indent + 1)));
				break;

			default:
				throw new IOException("Illegal character \"" + n
						+ "\" in JSON stream at " + getPosition());
			}
		}
	}

	private String getPosition() {

		return lineNumber + ":" + colNumber;
	}

	private static class Field implements Comparable<Field> {

		public final String key;
		public final String value;
		public final boolean isScalar;

		public Field(final String key, final String value,
				final boolean isScalar) {

			this.key = key;
			this.value = value;
			this.isScalar = isScalar;
		}

		@Override
		public int compareTo(final Field field) {

			return key.compareTo(field.key);
		}
	}
}
