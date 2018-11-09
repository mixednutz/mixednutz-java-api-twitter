package net.mixednutz.api.twitter.adapter;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.twitter.api.Twitter;

import net.mixednutz.api.adapter.model.IOauth1Credentials;
import net.mixednutz.api.core.adapter.AbstractSocialNetworkClient;
import net.mixednutz.api.twitter.client.TwitterAdapter;

public class TwitterService extends AbstractSocialNetworkClient<TwitterAdapter, IOauth1Credentials> {

	private ConnectionFactory<Twitter> connectionFactory;
	
	public TwitterService(ConnectionFactory<Twitter> connectionFactory) {
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
						createConnectionData(creds)).getApi());
	}
	
	protected ConnectionData createConnectionData(IOauth1Credentials creds) {
		return new ConnectionData(creds.getProviderId(), null, null, null, null, 
				creds.getAccessToken(), creds.getSecret(), null, null);
	}
	
	protected Connection<Twitter> createConnection(ConnectionData cd) {
		return connectionFactory.createConnection(cd);
	}

}
