package net.mixednutz.api.twitter.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.mixednutz.api.core.model.AlternateLink;
import net.mixednutz.api.core.model.ReshareCount;
import net.mixednutz.api.core.model.TimelineElement;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.twitter.TwitterFeedType;
import twitter4j.Status;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TweetElement extends TimelineElement implements ITimelineElement {
	
	private static final String APPLICATION_JSON_OEMBED = "application/json+oembed";
		
	private static TimelineElement.Type TYPE = new TimelineElement.Type(){
		TwitterFeedType feedType = TwitterFeedType.getInstance();
		@Override
		public String getName() {return "tweet";}
		@Override
		public String getNamespace() {return feedType.getHostName();}
		@Override
		public String getId() {return feedType.getId()+"_"+getName();}
		};
	
	FavoriteCount favorites;
	
	public TweetElement() {
		super();
	}

	public TweetElement(Status status) {
		super();
		this.setType(TYPE);
		this.setPostedByUser(new TwitterUser(status.getUser()));
		this.setPostedOnDate(ZonedDateTime.ofInstant(status.getCreatedAt().toInstant(), ZoneId.systemDefault()));
		this.setPaginationId(Long.toString(status.getId()));
		this.setDescription(status.getText());
		if (status.isRetweet()) {
			/*
			 * If it's a RT, keep user, time, and description of RT, use original tweet's info
			 * for provider ID, URL/URI and reshare info.
			 */
			status = status.getRetweetedStatus();
		}
		this.setProviderId(Long.toString(status.getId()));
		this.setUrl("https://twitter.com/"+status.getUser().getScreenName()+
				"/status/"+status.getId());
		this.setUri("/statuses/show/"+status.getId());
		this.setAlternateLinks(new ArrayList<>());
		this.getAlternateLinks().add(new AlternateLink("https://publish.twitter.com/oembed?url="+getUrl(),
				APPLICATION_JSON_OEMBED));
		this.setReshares(Collections.singletonList(
				new ReshareCount(Integer.valueOf(status.getRetweetCount()), 
						TwitterFeedType.getInstance())));
		this.favorites = new FavoriteCount(status.getFavoriteCount());
		
	}

}
