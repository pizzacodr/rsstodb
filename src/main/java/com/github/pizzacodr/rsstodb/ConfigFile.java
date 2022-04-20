package com.github.pizzacodr.rsstodb;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "file:${user.dir}/rsstodb.properties", 
"file:${user.home}/rsstodb.properties"})

public interface ConfigFile extends Config {
	
	@DefaultValue("https://feed.podbean.com/brbcpodcast/feed.xml")
	String feedUrl();
	
	@DefaultValue("jdbc:sqlite:${user.home}/podbean.sqlite")
	String dbFileLocation();
	
	@DefaultValue("false")
	boolean fullyPopulateTbl();
}
