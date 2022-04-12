package com.github.pizzacodr.rsstodb;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;

public class RssProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(RssProcessor.class);

	public void processFeed(ConfigFile configFile) throws SQLException, IllegalArgumentException, FeedException, IOException, InterruptedException {
		
		Database database = new Database(configFile.dbFileLocation());

		SyndFeedInput input = new SyndFeedInput();

		URL feedUrl = new URL(configFile.feedUrl());
		SyndFeed feed = input.build(new XmlReader(feedUrl));

		Episode episode = new Episode();

		List<SyndEntry> entriesList = feed.getEntries();
		for (SyndEntry eachEntry : entriesList) {
			
			UUID uuid = UUID.randomUUID();
			logger.debug(uuid.toString());
			episode.setUuid(uuid.toString());

			String title = eachEntry.getTitle();
			logger.debug(title);
			episode.setTitle(title);

			String link = eachEntry.getLink();
			logger.debug(link);
			episode.setLink(link);

			Date date = eachEntry.getPublishedDate();
			logger.debug(date.toString());
			episode.setDate(date.toString());
			
			episode.setSharelink(retrieveShareLink(link));
			
			List<SyndContent> contentsList = eachEntry.getContents();
			for (SyndContent eachContent : contentsList) {
				String contentWithoutTags = eachContent.getValue().replaceAll("<[^>]*>", "");
				logger.debug(contentWithoutTags);
				episode.setContent(contentWithoutTags);
			}

			if(database.insertIntoEpisodeIfNew(episode)) {
				break;
			}
		}
	}
	
	private String retrieveShareLink(String link) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).GET()
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		String pattern = "https://www.podbean.com/ew/.+?(?=\")";
		Pattern compiledPattern = Pattern.compile(pattern);
		
		Matcher m = compiledPattern.matcher(response.body());
		if (m.find()) {
			logger.debug(m.group(0));
			return m.group(0);
		} else {
			logger.debug("NO MATCH for the share link");
			throw new ShareLinkException("NO MATCH for the share link");
		}
	}
}
