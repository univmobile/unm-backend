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
import fr.univmobile.backend.domain.News;
import fr.univmobile.backend.domain.NewsRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.RestoMenu;
import fr.univmobile.backend.domain.RestoMenuRepository;

public class FeedUtils {

	@Autowired
	FeedRepository feedRepository;

	@Autowired
	NewsRepository newsRepository;

	@Autowired
	RestoMenuRepository restoMenuRepository;

	@Autowired
	PoiRepository poiRepository;

	private static final Log log = LogFactory.getLog(FeedUtils.class);

	private void persistRssFeed(Feed feed) {

		SyndFeed rss = null;
		InputStream is = null;

		try {
			String urlString = feed.getUrl();
			URLConnection connection = new URL(urlString).openConnection();
			is = connection.getInputStream();
			if ("gzip".equals(connection.getContentEncoding())) {
				is = new GZIPInputStream(is);
			}
			InputSource source = new InputSource(is);
			SyndFeedInput input = new SyndFeedInput();
			rss = input.build(source);

			if (rss != null) {

				List<SyndEntry> entries = rss.getEntries();
				for (SyndEntry se : entries){
					News newRssFeed = newsRepository.findByGuidAndFeed(se.getUri(), feed);
					if (newRssFeed == null)
						newRssFeed = new News();

					newRssFeed.setTitle(se.getTitle());
					newRssFeed.setLink(se.getUri());
					newRssFeed.setDescription(se.getDescription().getValue());
					newRssFeed.setAuthor(se.getAuthor());
					newRssFeed.setGuid(se.getUri());
					newRssFeed.setPublishedDate(se.getPublishedDate());

					newRssFeed.setFeed(feed);
					newsRepository.save(newRssFeed);

					List<SyndEnclosure> enclosures = se.getEnclosures();
					if (enclosures.size() > 0){
						if (enclosures.get(0).getType().contains("image/")){
							newRssFeed.setImageUrl(enclosures.get(0).getUrl());
						}
					}
				}
			}
			log.info("Rss object " + feed.getName() + " (" + feed.getUrl()+ ") processed successfully.");
		} catch (Throwable e) {
			log.error("Exception occured building the rss object " + feed.getName() + " (" + feed.getUrl()+ ")", e);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					log.error("Exception closing the inputStream", e);
				}
		}

	}

	private void persistArticleFeed(Feed feed) {

		try {
			String urlString = feed.getUrl();

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

					News newArticleFeed = newsRepository.findByGuidAndFeed(elem.getAttribute("id"), feed);

					if (newArticleFeed == null)
						newArticleFeed = new News();

					newArticleFeed.setTitle(elem.getAttribute("title"));
					newArticleFeed.setLink(urlString);
					newArticleFeed.setDescription(elem.getTextContent());
					newArticleFeed.setPublishedDate(date);
					newArticleFeed.setImageUrl(elem.getAttribute("image"));
					newArticleFeed.setGuid(elem.getAttribute("id"));
					newArticleFeed.setCategory(elem.getAttribute("category"));

					newArticleFeed.setFeed(feed);

					newsRepository.save(newArticleFeed);
				}

			}
		} catch (Exception e) {
			log.error("Exception occured building the article object", e);
		}
	}

	private void persistMenu(String urlString, List<Poi> childPois) {

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
					
					if (poi != null) {

						NodeList menues = elem.getElementsByTagName("menu");
	
						for (int j = 0; j < menues.getLength(); j++) {
							Element menuElem = (Element) menues.item(j);
	
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd", Locale.US);
							Date date = sdf.parse(menuElem.getAttribute("date"));
	
	
							RestoMenu restoMenu = restoMenuRepository.findByPoiAndEffectiveDate(poi, date);
							if (restoMenu == null){
								restoMenu = new RestoMenu();
								restoMenu.setPoi(poi);
								restoMenu.setEffectiveDate(date);
							}
							restoMenu.setDescription(menuElem.getTextContent());
	
							restoMenuRepository.save(restoMenu);
						}
						restoMenuRepository.flush();
					}
				}

			}
		} catch (Exception e) {
			log.error("Exception occured building the article object", e);
		}

	}

	private Poi poiByRestoId(List<Poi> pois, String restoId) {
		for (Poi poi : pois) {
			if (poi.getRestoId() != null && poi.getRestoId().equals(restoId)) {
				return poi;
			}
		}
		log.warn("Resto " + restoId + " not found.");
		return null;
	}

	public void persistRssFeeds() {
		for (Feed feed : feedRepository.findByActiveIsTrueAndType(Feed.Type.RSS)) {
			persistRssFeed(feed);
		}
	}

	public void persistCustomFeed(){
		for (Feed feed : feedRepository.findByActiveIsTrueAndType(Feed.Type.RESTO)) {
			persistArticleFeed(feed);
		}
	}

	public void persistRestoMenues(){
		for (Poi poi : poiRepository.findAllByRestoMenuUrlNotNullOrEmpty()) {
			//List<Poi> childPois = poiRepository.findByParent(poi);
			List<Poi> childPois = poiRepository.findAllChildRestaurant(poi.getLegacy());
			persistMenu(poi.getRestoMenuUrl(), childPois);
		}
	}
}
