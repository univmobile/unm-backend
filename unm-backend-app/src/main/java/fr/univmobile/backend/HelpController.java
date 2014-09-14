package fr.univmobile.backend;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.util.MarkdownToXHTMLConverter;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "help", "help/" })
public class HelpController extends AbstractBackendController {

	public HelpController() {

	}

	@Override
	public View action() throws IOException, TransactionException {

		getDelegationUser();

		// 1. HELP

		final String xhtml;

		final InputStream is = getServletContext().getResourceAsStream(
				"/WEB-INF/help/index.md");
		if (is == null) {
			throw new FileNotFoundException(
					"Cannot find resource: /WEB-INF/help/index.md");
		}
		try {

			xhtml = MarkdownToXHTMLConverter.convert(is, UTF_8);

		} finally {
			is.close();
		}

		setAttribute("htmlhelp", xhtml);

		// 9. END

		return new View("help.jsp");
	}
}
