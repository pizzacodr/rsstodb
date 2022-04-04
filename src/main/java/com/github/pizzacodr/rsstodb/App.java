package com.github.pizzacodr.rsstodb;

import java.io.IOException;
import java.sql.SQLException;
import org.aeonbits.owner.ConfigFactory;

import com.rometools.rome.io.FeedException;

public class App {
    public static void main( String[] args ) throws IllegalArgumentException, FeedException, IOException, InterruptedException, SQLException {
    	
    	ConfigFile configFile = ConfigFactory.create(ConfigFile.class, System.getProperties());
    	new RssProcessor().processFeed(configFile);
    }
}
