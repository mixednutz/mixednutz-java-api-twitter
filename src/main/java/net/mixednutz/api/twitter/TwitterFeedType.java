package net.mixednutz.api.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.mixednutz.api.core.model.NetworkInfoSmall;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TwitterFeedType extends NetworkInfoSmall {
	
	private static final String DISPLAY_NAME = "Twitter";
	private static final String HOST_NAME = "twitter.com";
	private static final String ID = "twitter";
	private static final String ICON_NAME = "twitter";
	
	private static TwitterFeedType instance;

	public TwitterFeedType() {
		super();
		this.setDisplayName(DISPLAY_NAME);
		this.setHostName(HOST_NAME);
		this.setId(ID);
		this.setFontAwesomeIconName(ICON_NAME);
	}

	public static TwitterFeedType getInstance() {
		if (instance==null) {
			instance = new TwitterFeedType();
		}
		return instance;
	}

	@Override
	public String[] compatibleMimeTypes() {
		return new String[] {
				"text/plain", //text and links
				"image/*" //images
				};
	}
	
	
}
