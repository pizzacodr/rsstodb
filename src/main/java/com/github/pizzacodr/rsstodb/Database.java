package com.github.pizzacodr.rsstodb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
	
	private static Logger logger = LoggerFactory.getLogger(Database.class);
	private Connection connection;
	
	public Database(String dbFileLocation) throws SQLException {
		connection = DriverManager.getConnection(dbFileLocation);
		try (Statement statement = connection.createStatement()) {
	        statement.executeUpdate("CREATE TABLE IF NOT EXISTS EPISODE (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
	        		+ "UUID TEXT NOT NULL, TITLE TEXT, LINK TEXT, CONTENT TEXT, SHARELINK TEXT, DATE TEXT);");
		}
	}
	
	@SuppressWarnings("java:S2095")
	public boolean insertIntoEpisodeIfNew(Episode episode) throws SQLException {
		
		PreparedStatement prepStm = connection.prepareStatement("SELECT COUNT(*) FROM EPISODE WHERE LINK = ?;");
        prepStm.setString(1, episode.getLink());
        ResultSet rs = prepStm.executeQuery();
		
		int fetchSize = 0;
		while (rs.next()) {
			logger.debug("ResultSet for Select statement: {}", rs.getInt(1));
			fetchSize = rs.getInt(1);
        }
		
		if (fetchSize == 0) {
			
			prepStm = connection.prepareStatement("INSERT INTO EPISODE VALUES(NULL, ?, ?, ?, ?, ?, ?);");
			prepStm.setString(1, episode.getUuid());
			prepStm.setString(2, episode.getTitle());
			prepStm.setString(3, episode.getLink());
			prepStm.setString(4, episode.getContent());
			prepStm.setString(5, episode.getSharelink());
			prepStm.setString(6, episode.getDate());
			prepStm.executeUpdate();
			
	        logger.debug("Inserted into DB \n");
		} else {
			logger.debug("Skipped Insert \n");
		}
        
		prepStm.close();
        return fetchSize == 0;
	}

}
