package net.mixednutz.api.twitter.client;

import org.springframework.social.connect.Connection;

import net.mixednutz.api.client.PostClient;
import net.mixednutz.api.twitter.model.TweetForm;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class StatusUpdateAdapter implements PostClient<TweetForm> {

	Connection<Twitter> conn;

	public StatusUpdateAdapter(Connection<Twitter> conn) {
		super();
		this.conn = conn;
	}

	@Override
	public void postToTimeline(TweetForm form) {
		StatusUpdate statusUpdate = form.toStatusUpdate();
		try {
			conn.getApi().tweets().updateStatus(statusUpdate);
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
