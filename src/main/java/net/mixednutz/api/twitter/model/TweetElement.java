package net.mixednutz.api.twitter.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
	
	private static final String APPLICATION_JSON_OEMBED = "application/json+oembed";
	
	private Status status;
	private String url;
	private String uri;
	private List<AlternateLink> alternateLinks = new ArrayList<>();
	
	private static ITimelineElement.Type TYPE = new ITimelineElement.Type(){
		@Override
		public String getName() {return "tweet";}
		@Override
		public String getNamespace() {return "twitter.com";}
		};
	
	
	public TweetElement(Status status) {
		super();
		this.status = status;
		this.url = "https://twitter.com/"+status.getUser().getScreenName()+
				"/status/"+status.getId();
		this.uri = "/statuses/show/"+status.getId();
		this.alternateLinks.add(new AlternateLink("https://publish.twitter.com/oembed?url="+url,
				APPLICATION_JSON_OEMBED));
	}

	@Override
	public Long getProviderId() {
		return Long.valueOf(status.getId());
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getUri() {
		return uri;
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
		return Long.valueOf(status.getId());
	}

	@Override
	public String getTitle() {
		return null;
	}


	@Override
	public String getDescription() {
		return status.getText();
	}

	@Override
	public Collection<AlternateLink> getAlternateLinks() {
		return this.alternateLinks;
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
	
	public static class AlternateLink implements IAlternateLink {

		private String href;
		private String type;

		public AlternateLink(String href, String type) {
			super();
			this.href = href;
			this.type = type;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
	}
	
}
