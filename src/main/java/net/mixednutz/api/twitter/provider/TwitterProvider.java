package net.mixednutz.api.twitter.provider;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;

import net.mixednutz.api.core.provider.AbstractApiProvider;
import net.mixednutz.api.provider.IOauth1Credentials;
import net.mixednutz.api.twitter.TwitterFeedType;
import net.mixednutz.api.twitter.client.TwitterAdapter;
import twitter4j.Twitter;

public class TwitterProvider extends AbstractApiProvider<TwitterAdapter, IOauth1Credentials> {

	private ConnectionFactory<Twitter> connectionFactory;
	
	public TwitterProvider(ConnectionFactory<Twitter> connectionFactory) {
		super(TwitterAdapter.class);
		this.connectionFactory = connectionFactory;
	}

	@Override
	public String getProviderId() {
		return connectionFactory.getProviderId();
	}

	@Override
	public TwitterAdapter getApi(IOauth1Credentials creds) {
		return new TwitterAdapter(
				createConnection(
						createConnectionData(creds)));
	}
	
	protected ConnectionData createConnectionData(IOauth1Credentials creds) {
		return new ConnectionData(creds.getProviderId(), null, null, null, null, 
				creds.getAccessToken(), creds.getSecret(), null, null);
	}
	
	protected Connection<Twitter> createConnection(ConnectionData cd) {
		return connectionFactory.createConnection(cd);
	}

	@Override
	public TwitterFeedType getNetworkInfo() {
		return new TwitterFeedType();
	}

}
