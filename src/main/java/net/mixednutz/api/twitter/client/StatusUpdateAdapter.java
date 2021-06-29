package net.mixednutz.api.twitter.client;

import java.util.Collections;
import java.util.Map;

import org.springframework.social.connect.Connection;

import net.mixednutz.api.client.PostClient;
import net.mixednutz.api.twitter.model.TweetElement;
import net.mixednutz.api.twitter.model.TweetForm;
import twitter4j.Status;
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
	public TweetElement postToTimeline(TweetForm form) {
		StatusUpdate statusUpdate = form.toStatusUpdate();
		try {
			Status status = conn.getApi().tweets().updateStatus(statusUpdate);
			return new TweetElement(status);
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public TweetForm create() {
		return new TweetForm();
	}

	@Override
	public Map<String, Object> referenceDataForPosting() {
		return Collections.emptyMap();
	}
	
}
