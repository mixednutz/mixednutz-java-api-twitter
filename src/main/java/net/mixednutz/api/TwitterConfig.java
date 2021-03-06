package net.mixednutz.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.social.connect.web.CredentialsCallback;
import org.springframework.social.connect.web.CredentialsInterceptor;
import org.springframework.social.twitter4j.connect.TwitterConnectionFactory;

import net.mixednutz.api.provider.IOauth1Credentials;
import net.mixednutz.api.twitter.provider.TwitterProvider;
import twitter4j.Twitter;

@Profile("twitter")
@Configuration
@ConfigurationProperties(prefix="mixednutz.social")
public class TwitterConfig {
	
	private TwitterConnectionProperties twitter= new TwitterConnectionProperties();
	
	@Bean
	public TwitterConnectionFactory twitterConnectionFactory() {
		return new TwitterConnectionFactory(twitter.consumerKey, 
				twitter.consumerSecret);
	}
	@Bean
	public TwitterProvider twitterService() {
		return new TwitterProvider(twitterConnectionFactory());
	}
	
	@Bean
	public CredentialsInterceptor<Twitter, IOauth1Credentials> twitterCredentialsInterceptor(CredentialsCallback callback) {
		return new TwitterCredentialsInterceptor(callback);
	}

	public TwitterConnectionProperties getTwitter() {
		return twitter;
	}

	public void setTwitter(TwitterConnectionProperties twitter) {
		this.twitter = twitter;
	}
	
	public static class TwitterCredentialsInterceptor extends CredentialsInterceptor<Twitter, IOauth1Credentials> {

		public TwitterCredentialsInterceptor(CredentialsCallback callback) {
			super(Twitter.class, IOauth1Credentials.class, callback);
		}
		
	}

	public static class TwitterConnectionProperties {
		
		private String consumerKey;
		private String consumerSecret;
		
		public String getConsumerKey() {
			return consumerKey;
		}
		public void setConsumerKey(String consumerKey) {
			this.consumerKey = consumerKey;
		}
		public String getConsumerSecret() {
			return consumerSecret;
		}
		public void setConsumerSecret(String consumerSecret) {
			this.consumerSecret = consumerSecret;
		}
		
		
	}
}
