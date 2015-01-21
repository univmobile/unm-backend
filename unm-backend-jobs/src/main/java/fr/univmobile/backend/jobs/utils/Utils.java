package fr.univmobile.backend.jobs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import fr.univmobile.backend.domain.Feed;
import fr.univmobile.backend.domain.FeedRepository;
import fr.univmobile.backend.jobs.domain.New;
import fr.univmobile.backend.jobs.domain.NewRepository;

public class Utils {

	@Autowired
	NewRepository newRepository;

	@Autowired
	FeedRepository feedRepository;

	private static final Log log = LogFactory.getLog(Utils.class);

	public void feedRss() throws IOException {
		for (Feed feed : feedRepository.findAll()) {

			if (feed.getType().equals(Feed.Type.RSS)) {

				String url = feed.getUrl();
				SyndFeed rss = null;
				InputStream is = null;

				try {

					URLConnection openConnection = new URL(url)
							.openConnection();
					is = new URL(url).openConnection().getInputStream();
					if ("gzip".equals(openConnection.getContentEncoding())) {
						is = new GZIPInputStream(is);
					}
					InputSource source = new InputSource(is);
					SyndFeedInput input = new SyndFeedInput();
					rss = input.build(source);

					New newRssFeed = new New();
					newRssFeed.setTitle(rss.getTitle());
					newRssFeed.setLink(rss.getUri());
					newRssFeed.setDescription(rss.getDescription());
					newRssFeed.setAuthor(rss.getAuthor());
					newRssFeed.setGuid(rss.getUri());
					newRssFeed.setPublishedDate(rss.getPublishedDate());

					List<SyndEntry> items = rss.getEntries();
					if (items.size() > 0) {
						List<SyndEnclosure> enclosures = items.get(0).getEnclosures();
						if (enclosures.get(0).getType().contains("image/"))
							newRssFeed.setImageUrl(enclosures.get(0).getUrl());
					}

				}

				catch (Exception e) {
					log.error(
							"Exception occured when building the feed object out of the url",
							e);
				} finally {
					if (is != null)
						is.close();
				}
			} else if (feed.getType().equals(Feed.Type.RESTO)) {
				// FIXME: make other parser
				// New newRssFeed = new New();
				// newRssFeed.setTitle(title);
				// newRssFeed.setLink(link);
				// newRssFeed.setDescription(description);
				// newRssFeed.setAuthor(author);
				// newRssFeed.setGuid(guid);
				// newRssFeed.setPublishedDate(publishedDate);
				// newRssFeed.setImageUrl(imageUrl);
				// newRssFeed.setRestoId(restoId);
				// newRssFeed.setCategory(category);
			}
		}
	}
}
