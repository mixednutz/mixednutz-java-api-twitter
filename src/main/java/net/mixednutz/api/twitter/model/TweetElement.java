package net.mixednutz.api.twitter.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.mixednutz.api.model.IAction;
import net.mixednutz.api.model.IAlternateLink;
import net.mixednutz.api.model.IGroupSmall;
import net.mixednutz.api.model.IReactionCount;
import net.mixednutz.api.model.ITagCount;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.twitter.TwitterFeedType;
import twitter4j.Status;

public class TweetElement implements ITimelineElement {
	
	private Status status;
	private static ITimelineElement.Type TYPE = new ITimelineElement.Type(){
		@Override
		public String getName() {return "tweet";}
		@Override
		public String getNamespace() {return "twitter.com";}
		};
	
	
	public TweetElement(Status status) {
		super();
		this.status = status;
	}

	@Override
	public String getUrl() {
		return "https://twitter.com/"+status.getUser().getScreenName()+
				"/status/"+status.getId();
	}

	@Override
	public String getUri() {
		return null;
	}

	@Override
	public List<? extends IAction> getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		return TYPE;
	}

	@Override
	public TwitterUser getPostedByUser() {
		return new TwitterUser(status.getUser());
	}

	@Override
	public IGroupSmall getPostedToGroup() {
		return null;
	}

	@Override
	public ZonedDateTime getPostedOnDate() {
		return ZonedDateTime.ofInstant(status.getCreatedAt().toInstant(), ZoneId.systemDefault());
	}

	@Override
	public ZonedDateTime getUpdatedOnDate() {
		return null;
	}


	@Override
	public Long getPaginationId() {
		return status.getId();
	}

	@Override
	public String getTitle() {
		return null;
	}


	@Override
	public String getDescription() {
		return status.getUser().getScreenName()+": "+status.getText();
	}

	@Override
	public Collection<? extends IAlternateLink> getAlternateLinks() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<? extends IReactionCount> getReactions() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<? extends ITagCount> getTags() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<RetweetCount> getReshares() {
		return Collections.singletonList(
				new RetweetCount(status.getRetweetCount(), 
						TwitterFeedType.getInstance()));
	}
	
	public FavoriteCount getFavorites() {
		return new FavoriteCount(status.getFavoriteCount());
	}

}
