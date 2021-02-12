package net.mixednutz.api.twitter.model;

import net.mixednutz.api.model.IPost;
import twitter4j.StatusUpdate;

public class TweetForm implements IPost {
	
	String status;
	
	public TweetForm(String status) {
		super();
		this.status = status;
	}

	public StatusUpdate toStatusUpdate() {
		StatusUpdate statusUpdate = new StatusUpdate(status);
		return statusUpdate;
	}
	
}
