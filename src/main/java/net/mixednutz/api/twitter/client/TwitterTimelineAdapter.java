package net.mixednutz.api.twitter.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.Tweet;

import net.mixednutz.api.client.TimelineClient;
import net.mixednutz.api.core.model.Page;
import net.mixednutz.api.core.model.PageRequest;
import net.mixednutz.api.model.IPage;
import net.mixednutz.api.model.IPageRequest;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.SortDirection;
import net.mixednutz.api.twitter.model.TweetElement;
import net.mixednutz.api.twitter.model.TwitterPagination;


public class TwitterTimelineAdapter implements TimelineClient<Long> {
	
	TimelineOperations internal;
	
	
	/* Rate limits */
//	private Long nextSearchRequest=0L;
	private Long nextHomeRequest=0L;
//	private Long nextShowRequest=0L;

	public TwitterTimelineAdapter(TimelineOperations internal) {
		super();
		this.internal = internal;
	}

	@Override
	public Page<TweetElement, Long> getTimeline() {
		return getTimeline(null);
	}

	@Override
	public Page<TweetElement, Long> getTimeline(IPageRequest<Long> pagination) {
		TwitterPagination twitterPagination = toTwitterPagination(pagination);
		
		//TODO Handle search hashtags
		
		if (twitterPagination.getMaxId()==0 && twitterPagination.getSinceId()>0) {
			//Only sleep when checking for newer items
			sleepUntilNextAllowedHomeRequest();
		}
		
		//We add +5 to the count because the count is not guaranteed
		//according to twitter api, deleted tweets get removed after the
		//count is applied
		List<Tweet> response;
		if (twitterPagination.getPageSize()!=null && 
				(twitterPagination.getSinceId()>0 || 
				twitterPagination.getMaxId()>0)) {
			response = internal.getHomeTimeline(twitterPagination.getPageSize()+5, 
					twitterPagination.getSinceId(), twitterPagination.getMaxId());
		} else if (twitterPagination.getPageSize()!=null) {
			response = internal.getHomeTimeline(twitterPagination.getPageSize()+5);
		} else {
			response = internal.getHomeTimeline();
		}
		updateHomeRateLimit();
		
		List<Tweet> results = new ArrayList<>();
		for (Tweet tweet: response) {
			//TODO do filter for hashtag
			results.add(tweet);
		}
		
		//Remember when we added +5? Let's
		//trim to ensure results match original page count
		if (!results.isEmpty() && 
				twitterPagination.getPageSize()>0 && results.size()>twitterPagination.getPageSize()) {
			results = results.subList(0, twitterPagination.getPageSize());
		}
			
		return toPage(results, twitterPagination);
	}

	@Override
	public IPage<? extends ITimelineElement, Long> getPublicTimeline() {
		return getPublicTimeline(null);
	}

	@Override
	public IPage<? extends ITimelineElement, Long> getPublicTimeline(IPageRequest<Long> pagination) {
		// TODO Auto-generated method stub
		return null;
	}

	TwitterPagination toTwitterPagination(IPageRequest<Long> pageRequest) {
		TwitterPagination twitterPagination = new TwitterPagination();
		if (pageRequest!=null) {
			twitterPagination.setPageSize(pageRequest.getPageSize());
			if (pageRequest.getSortDirection()==SortDirection.DESC){
				
				//Validate
				if (pageRequest.getStart()==null || pageRequest.getEnd()==null ||
						pageRequest.getStart()>pageRequest.getEnd()) {
					
					if (pageRequest.getStart()!=null) {
						twitterPagination.setMaxId(pageRequest.getStart());
					}
					if (pageRequest.getEnd()!=null) {
						twitterPagination.setSinceId(pageRequest.getEnd());
					}	
				}
			} else {
				throw new UnsupportedOperationException("Twitter doesnt do chronological sort");
			}
		} 		
		
		return twitterPagination;
	}
	
	PageRequest<Long> toPageRequest(Integer pageSize, Long maxId, Long sinceId) {
		PageRequest<Long> pageRequest = new PageRequest<>();
		pageRequest.setSortDirection(SortDirection.DESC);
		pageRequest.setPageSize(pageSize);
		if (maxId!=null) {
			pageRequest.setStart(maxId);
		}
		if (sinceId!=null) {
			pageRequest.setEnd(sinceId);
		}
		return pageRequest;
	}
	
	Page<TweetElement, Long> toPage(List<Tweet> items, TwitterPagination prevPage) {
		LinkedList<TweetElement> newItemList = new LinkedList<>();
		for (Tweet item: items) {
			newItemList.add(new TweetElement(item));
		}
				
		Page<TweetElement, Long> newPage = new Page<>();
		newPage.setItems(newItemList);
		if (prevPage!=null) {
			newPage.setPageRequest(toPageRequest(prevPage.getPageSize(), 
					prevPage.getMaxId(), prevPage.getSinceId()));
		}
		if (!items.isEmpty()) {
			int pageSize =prevPage!=null&&prevPage.getPageSize()!=null?prevPage.getPageSize():items.size();
			newPage.setNextPage(toPageRequest(pageSize, 
					newItemList.getLast().getPaginationId(), null));
			newPage.setHasNext(true);
			newPage.setPrevPage(toPageRequest(pageSize, 
					null, newItemList.getFirst().getPaginationId()));
			newPage.setHasPrev(true);
		}
		return newPage;
	}
	
	private static synchronized void updateHomeRateLimit() {
		//nextHomeRequest.put(conn, getNextRequest(rateLimitStatus));
	}
	
	private static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}
	private static void sleepUntil(long time) {
		long ms = time - System.currentTimeMillis();
		if (ms>0) {
			sleep(ms);
		}
	}
//	private void sleepUntilNextAllowedSearchRequest() {
//		sleepUntil(nextSearchRequest);
//	}
	private void sleepUntilNextAllowedHomeRequest() {
		synchronized (internal) {
			sleepUntil(nextHomeRequest);
		}
	}	
//	private void sleepUntilNextAllowedShowRequest() {
//		sleepUntil(nextShowRequest);
//	}	

}
