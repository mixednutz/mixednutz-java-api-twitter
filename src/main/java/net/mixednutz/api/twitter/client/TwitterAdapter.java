package net.mixednutz.api.twitter.client;

import org.springframework.social.connect.Connection;

import net.mixednutz.api.client.GroupClient;
import net.mixednutz.api.client.MixednutzClient;
import net.mixednutz.api.client.TimelineClient;
import net.mixednutz.api.client.UserClient;
import twitter4j.Twitter;

/**
 * Adapter around the native Twitter client so we can use the MixednutzClient API
 * 
 * @author apfesta
 *
 */
public class TwitterAdapter implements MixednutzClient {
	
	private Connection<Twitter> conn;
	private TwitterTimelineAdapter timelineAdapter;

	public TwitterAdapter(Connection<Twitter> conn) {
		super();
		this.conn = conn;
		initSubApis();
	}

	@Override
	public GroupClient getGroupClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimelineClient<Long> getTimelineClient() {
		return timelineAdapter;
	}

	@Override
	public UserClient<Long> getUserClient() {
		return timelineAdapter;
	}

	private void initSubApis() {
		timelineAdapter = new TwitterTimelineAdapter(conn);
	}
	

}
