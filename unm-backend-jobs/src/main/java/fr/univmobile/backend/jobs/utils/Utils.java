package fr.univmobile.backend.jobs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import fr.univmobile.backend.domain.Feed;
import fr.univmobile.backend.domain.FeedRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.jobs.domain.New;
import fr.univmobile.backend.jobs.domain.NewRepository;
import fr.univmobile.backend.jobs.domain.RestoMenu;
import fr.univmobile.backend.jobs.domain.RestoMenuRepository;

public class Utils {

	@Autowired
	FeedRepository feedRepository;

	@Autowired
	NewRepository newRepository;

	@Autowired
	RestoMenuRepository restoMenuRepository;

	@Autowired
	PoiRepository poiRepository;

	private static final Log log = LogFactory.getLog(Utils.class);

	public void persistRssFeed(String urlString) {

		SyndFeed rss = null;
		InputStream is = null;

		try {

			URLConnection openConnection = new URL(urlString).openConnection();
			is = new URL(urlString).openConnection().getInputStream();
			if ("gzip".equals(openConnection.getContentEncoding())) {
				is = new GZIPInputStream(is);
			}
			InputSource source = new InputSource(is);
			SyndFeedInput input = new SyndFeedInput();
			rss = input.build(source);

			if (rss != null) {

				New newRssFeed = new New();
				newRssFeed.setTitle(rss.getTitle());
				newRssFeed.setLink(rss.getUri());
				newRssFeed.setDescription(rss.getDescription());
				newRssFeed.setAuthor(rss.getAuthor());
				newRssFeed.setGuid(rss.getUri());
				newRssFeed.setPublishedDate(rss.getPublishedDate());

				List<SyndEntry> items = rss.getEntries();
				if (items.size() > 0) {
					List<SyndEnclosure> enclosures = items.get(0)
							.getEnclosures();
					if (enclosures.get(0).getType().contains("image/"))
						newRssFeed.setImageUrl(enclosures.get(0).getUrl());
				}

			}

		} catch (Exception e) {
			log.error("Exception occured building the rss object", e);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					log.error("Exception closing the inputStream", e);
				}
		}

	}

	public void persistArticleFeed(String urlString) {

		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(conn.getInputStream());

			NodeList nodeList = doc.getDocumentElement().getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
							Locale.US);
					Date date = sdf.parse(elem.getAttribute("date"));

					New newArticleFeed = newRepository.findByLinkAndId(
							urlString, elem.getAttribute("id"));
					if (newArticleFeed == null)
						newArticleFeed = new New();

					newArticleFeed.setTitle(elem.getAttribute("title"));
					newArticleFeed.setLink(urlString);
					newArticleFeed.setDescription(elem.getTextContent());
					newArticleFeed.setPublishedDate(date);
					newArticleFeed.setImageUrl(elem.getAttribute("image"));
					newArticleFeed.setRestoId(elem.getAttribute("id"));
					newArticleFeed.setCategory(elem.getAttribute("category"));

					newRepository.save(newArticleFeed);
				}

			}
		} catch (Exception e) {
			log.error("Exception occured building the article object", e);
		}

	}

	public void persistMenu(String urlString, List<Poi> childPois) {

		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(conn.getInputStream());

			NodeList nodeList = doc.getDocumentElement().getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;

					String restoId = elem.getAttribute("id");
					
					Poi poi = poiByRestoId(childPois, restoId);

					NodeList menues = elem.getElementsByTagName("menu");

					for (int j = 0; j < menues.getLength(); j++) {
						Element menuElem = (Element) menues.item(j);

						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd", Locale.US);
						Date date = sdf.parse(menuElem.getAttribute("date"));

						RestoMenu restoMenu = new RestoMenu();

						restoMenu.setPoi(poi);
						restoMenu.setEffectiveDate(date);
						restoMenu.setDescription(menuElem.getTextContent());

						restoMenuRepository.save(restoMenu);
					}
				}

			}
		} catch (Exception e) {
			log.error("Exception occured building the article object", e);
		}

	}
	
	public Poi poiByRestoId(List<Poi> pois, String restoId) {
		for (Poi poi : pois)
			if (poi.getRestoId().equals(restoId))
				return poi;
		return null;
	}

	public void persistFeeds() {
		for (Feed feed : feedRepository.findAll()) {

			if (feed.getType().equals(Feed.Type.RSS)) {

				persistRssFeed(feed.getUrl());

			} else if (feed.getType().equals(Feed.Type.RESTO)) {

				persistArticleFeed(feed.getUrl());

			}

			for (Poi poi : poiRepository.findAll()) {
				if (poi.getRestoMenuUrl() != null) {
					List<Poi> childPois = poiRepository.findByParent(poi);
					persistMenu(poi.getRestoMenuUrl(), childPois);
					
				}
			}
		}
	}
}
