package net.mixednutz.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import net.mixednutz.api.twitter.adapter.TwitterService;

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
	public TwitterService twitterService() {
		return new TwitterService(twitterConnectionFactory());
	}

	public TwitterConnectionProperties getTwitter() {
		return twitter;
	}

	public void setTwitter(TwitterConnectionProperties twitter) {
		this.twitter = twitter;
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
