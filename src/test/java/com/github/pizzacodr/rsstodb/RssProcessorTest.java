package com.github.pizzacodr.rsstodb;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Config.DefaultValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

class RssProcessorTest {
	
	private WireMockServer wireMockServer = new WireMockServer();
	
	@TempDir
	static Path tempDir;
	
	interface ConfigFileTest extends ConfigFile {

		@Override
		@DefaultValue("http://localhost:8080/feed.xml")
		public String feedUrl();
		
	}
	
	@Test
	void testProcessFeed() throws IOException, IllegalArgumentException, SQLException, FeedException, InterruptedException {
		
		wireMockServer.start();

		configureFor("localhost", 8080);
		
        ClassLoader classLoader = this.getClass().getClassLoader();
        File feedFile = new File(classLoader.getResource("feed.xml").getFile());
        
        String feedBodyResponse = new String (Files.readAllBytes(feedFile.toPath()));
        stubFor(get(urlEqualTo("/feed.xml")).willReturn(aResponse().withBody(feedBodyResponse)));
        
        File htmlFile = new File(classLoader.getResource("until-we-reach-our-homeland.html").getFile());
        String htmlBodyResponse = new String (Files.readAllBytes(htmlFile.toPath()));
        stubFor(get(urlEqualTo("/until-we-reach-our-homeland")).willReturn(aResponse().withBody(htmlBodyResponse)));
        
        ConfigFile configFileTest = ConfigFactory.create(ConfigFileTest.class, System.getProperties());
    	new RssProcessor().processFeed(configFileTest);
    	
    	Connection connection = DriverManager.getConnection(configFileTest.dbFileLocation());
    	Statement statement = connection.createStatement();
    	ResultSet rs = statement.executeQuery("SELECT * FROM EPISODE WHERE TITLE = 'Until We Reach our Homeland';");
    	
    	String titleFromDB = "";
    	while (rs.next()) {
    		titleFromDB = rs.getString("title");
	    }
    	
    	System.out.println(titleFromDB);
    	
    	assertTrue(titleFromDB.contains("Until We Reach our Homeland"), "DB Check Failed");
    	
    	wireMockServer.stop();
	}
}