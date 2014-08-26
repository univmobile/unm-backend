package fr.univmobile.backend;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.web.commons.AbstractController;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "comment" })
public class CommentController extends AbstractController {

	// public CommentController() {
	//
	// }

	@Override
	public View action() throws IOException {

		log.info("action()...");

		final PostComment postComment = getHttpInputs(PostComment.class);

		if (postComment.isHttpValid()) {

			final String username = postComment.username();
			final String message = postComment.message();

			log.info("username: " + username + ", message: " + message);

			throw new NotImplementedException();
		}

		return new View("text/plain", UTF_8, Locale.ENGLISH, "comment.jsp");
	}

	private static final Log log = LogFactory.getLog(CommentController.class);

	@HttpMethods("POST")
	interface PostComment extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String username();

		@HttpRequired
		@HttpParameter
		String message();
	}
}
