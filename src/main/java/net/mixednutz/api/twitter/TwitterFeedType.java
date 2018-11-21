package net.mixednutz.api.twitter;

import net.mixednutz.api.model.INetworkInfoSmall;

public class TwitterFeedType implements INetworkInfoSmall {
	
	private static final String DISPLAY_NAME = "Twitter";
	private static final String HOST_NAME = "twitter.com";
	private static final String ICON_NAME = "twitter";
	
	private static TwitterFeedType instance;

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public String getHostName() {
		return HOST_NAME;
	}

	@Override
	public String getFontAwesomeIconName() {
		return ICON_NAME;
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
