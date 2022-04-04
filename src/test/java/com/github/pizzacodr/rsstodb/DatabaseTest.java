package com.github.pizzacodr.rsstodb;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


class DatabaseTest {
	
	@TempDir
	static Path sharedTempDir;

	@Test
	void testDatabase() throws SQLException {
		
		String dbFileConnection = "jdbc:sqlite:" + sharedTempDir.toAbsolutePath().toString() + File.separator + "podbean.sqlite";
		new Database(dbFileConnection);
		
		File dbFile = new File(sharedTempDir.toAbsolutePath().toString() + File.separator + "podbean.sqlite");
		assertTrue(dbFile.exists(), "DB File was NOT created");
		
		
		Connection connection = DriverManager.getConnection(dbFileConnection);
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='EPISODE';");
		
		int fetchSize = 0;
		while (rs.next()) {
			fetchSize = rs.getInt(1);
        }
		assertEquals(1, fetchSize ,"Table NOT created");
		
		
		List<String> tblListfromQuery = new ArrayList<String>();
		rs = statement.executeQuery("pragma table_info ('episode');");
		while (rs.next()) {
			tblListfromQuery.add(rs.getString("name"));
	    }
		
		List<String> tblListProvided = new ArrayList<String>();
		tblListProvided.add("ID");
		tblListProvided.add("TITLE");
		tblListProvided.add("LINK");
		tblListProvided.add("CONTENT");
		tblListProvided.add("SHARELINK");
		tblListProvided.add("DATE");
		
		assertTrue(tblListfromQuery.equals(tblListProvided),"The columns in the Table DO NOT MATCH with expected columns from CREATE STM");	
	}

	@Test
	void testInsertIntoEpisodeIfNew() throws SQLException {
		String dbFileConnection = "jdbc:sqlite:" + sharedTempDir.toAbsolutePath().toString() + File.separator + "podbean.sqlite";
		Database db = new Database(dbFileConnection);
		
		Episode testEpisode = new Episode();
		testEpisode.setContent("testContent");
		testEpisode.setDate("testDate");
		testEpisode.setLink("testLink");
		testEpisode.setSharelink("testSharedLink");
		testEpisode.setTitle("testTitle");
		
		assertTrue(db.insertIntoEpisodeIfNew(testEpisode),"Initial insert FAILED");
		assertFalse(db.insertIntoEpisodeIfNew(testEpisode),"Second insert of same data should have failed but DIDN'T");
	}

}
