package com.github.pizzacodr.rsstodb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.rometools.rome.io.FeedException;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

class RssProcessorTest {
	
	private WireMockServer wireMockServer = new WireMockServer();
	
	@Test
	void testProcessFeed(@TempDir Path tempDir) throws IOException, IllegalArgumentException, SQLException, FeedException, InterruptedException {
		
		wireMockServer.start();

		configureFor("localhost", 8080);
		
        ClassLoader classLoader = this.getClass().getClassLoader();
        File feedFile = new File(classLoader.getResource("feed.xml").getFile());
        
        String feedBodyResponse = new String (Files.readAllBytes(feedFile.toPath()));
        stubFor(get(urlEqualTo("/feed.xml")).willReturn(aResponse().withBody(feedBodyResponse)));
        
        File htmlFile = new File(classLoader.getResource("until-we-reach-our-homeland.html").getFile());
        String htmlBodyResponse = new String (Files.readAllBytes(htmlFile.toPath()));
        stubFor(get(urlEqualTo("/until-we-reach-our-homeland")).willReturn(aResponse().withBody(htmlBodyResponse)));
        
        ConfigFile cfgMock = mock(ConfigFile.class);
        when(cfgMock.feedUrl()).thenReturn("http://localhost:8080/feed.xml");
        
        String dbFileConnection = "jdbc:sqlite:" + tempDir.toAbsolutePath().toString() + File.separator + "podbean.sqlite";
        when(cfgMock.dbFileLocation()).thenReturn(dbFileConnection); 
        
    	new RssProcessor().processFeed(cfgMock);
    	
    	Connection connection = DriverManager.getConnection(dbFileConnection);
    	Statement statement = connection.createStatement();
    	ResultSet rs = statement.executeQuery("SELECT * FROM EPISODE WHERE TITLE = 'Until We Reach our Homeland';");
    	
    	String titleFromDB = "";
    	while (rs.next()) {
    		titleFromDB = rs.getString("title");
	    }
    	
    	assertTrue(titleFromDB.contains("Until We Reach our Homeland"), "DB Check Failed");
    	
    	wireMockServer.stop();
	}
}
