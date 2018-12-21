package net.mixednutz.api.twitter.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.connect.Connection;

import net.mixednutz.api.client.TimelineClient;
import net.mixednutz.api.core.model.Page;
import net.mixednutz.api.core.model.PageRequest;
import net.mixednutz.api.model.IPage;
import net.mixednutz.api.model.IPageRequest;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.SortDirection;
import net.mixednutz.api.twitter.model.TweetElement;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class TwitterTimelineAdapter implements TimelineClient<Long> {
	
	private static final Log LOG = LogFactory.getLog(TwitterTimelineAdapter.class);
	
//	private static Map<Connection<Twitter>, Long> nextSearchRequest=
//			new HashMap<Connection<Twitter>, Long>();
	private static Map<Connection<Twitter>, Long> nextHomeRequest=
			new HashMap<Connection<Twitter>, Long>();
//	private static Map<Connection<Twitter>, Long> nextShowRequest=
//			new HashMap<Connection<Twitter>, Long>();
	
	/**
	 * Sorts Twitter Status reverse chronologically (newest first)
	 */
	protected Comparator<Status> statusComparator = new Comparator<Status>() {
		@Override
		public int compare(Status o1, Status o2) {
			return o1.compareTo(o2);
		}};
	

	Connection<Twitter> conn;

	public TwitterTimelineAdapter(Connection<Twitter> conn) {
		super();
		this.conn = conn;
	}

	@Override
	public Page<TweetElement, Long> getTimeline() {
		return getTimeline(null);
	}

	@Override
	public Page<TweetElement, Long> getTimeline(IPageRequest<Long> pagination) {
		Paging paging = toTwitterPaging(pagination);
		
		//TODO Handle search hashtags
				
		try {
			ResponseList<Status> response;
			
			//We add +5 to the count because the count is not guaranteed
			//according to twitter api, deleted tweets get removed after the
			//count is applied
			paging.setCount(pagination!=null&&pagination.getPageSize()>0?pagination.getPageSize()+5:20);
			
			if (paging.getMaxId()==0 && paging.getSinceId()>0) {
				//Only sleep when checking for newer items
				sleepUntilNextAllowedHomeRequest(conn);
			}
			
			response = conn.getApi().getHomeTimeline(paging);
			updateHomeRateLimit(conn, response.getRateLimitStatus());
			
			List<Status> results = new ArrayList<Status>();
			for (Status status: response) {
				//TODO do filter for hashtag
				results.add(status);
			}
			
			//Remember when we added +5? Let's
			//trim to ensure results match original page count
			if (!results.isEmpty() && 
					pagination!=null && pagination.getPageSize()>0 && results.size()>pagination.getPageSize()) {
				results = results.subList(0, pagination.getPageSize());
				paging.setCount(pagination.getPageSize());
			}
						
			//Wrap in Page
			return toPage(results, paging);
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		}
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

	Paging toTwitterPaging(IPageRequest<Long> pageRequest) {
		Paging paging = new Paging();
		if (pageRequest!=null) {
			paging.setCount(pageRequest.getPageSize());
			
			if (pageRequest.getSortDirection()==SortDirection.DESC){
				
				//Validate
				if (pageRequest.getStart()==null || pageRequest.getEnd()==null ||
						pageRequest.getStart()>pageRequest.getEnd()) {
					
					if (pageRequest.getStart()!=null) {
						paging.setMaxId(pageRequest.getStart());
					}
					if (pageRequest.getEnd()!=null) {
						paging.setSinceId(pageRequest.getEnd());
					}	
				}
			} else {
				throw new UnsupportedOperationException("Twitter doesnt do chronological sort");
			}
		}
				
		return paging;
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
	
	Page<TweetElement, Long> toPage(List<Status> items, Paging prevPage) {
		LinkedList<TweetElement> newItemList = new LinkedList<>();
		for (Status item: items) {
			newItemList.add(new TweetElement(item));
		}
				
		Page<TweetElement, Long> newPage = new Page<>();
		newPage.setItems(newItemList);
		if (prevPage!=null) {
			newPage.setPageRequest(toPageRequest(prevPage.getCount(), 
					prevPage.getMaxId(), prevPage.getSinceId()));
		}
		if (!items.isEmpty()) {
			int pageSize =prevPage!=null&&prevPage.getCount()>0?prevPage.getCount():items.size();
			newPage.setNextPage(toPageRequest(pageSize, 
					newItemList.getLast().getPaginationId(), null));
			newPage.setHasNext(true);
			newPage.setPrevPage(toPageRequest(pageSize, 
					null, newItemList.getFirst().getPaginationId()));
			newPage.setHasPrev(true);
		}
		return newPage;
	}
	
//	private static synchronized void updateSearchRateLimit(Connection<Twitter> conn, 
//			RateLimitStatus rateLimitStatus) {
//		nextSearchRequest.put(conn, getNextRequest(rateLimitStatus));
//	}
	
	private static synchronized void updateHomeRateLimit(Connection<Twitter> conn, 
			RateLimitStatus rateLimitStatus) {
		nextHomeRequest.put(conn, getNextRequest(rateLimitStatus));
	}
	
//	private static synchronized void updateShowRateLimit(Connection<Twitter> conn, 
//			RateLimitStatus rateLimitStatus) {
//		nextShowRequest.put(conn, getNextRequest(rateLimitStatus));
//	}
	
	private static long getNextRequest(RateLimitStatus rateLimitStatus) {
		long now = System.currentTimeMillis();
		int searchRequestsRemaining = rateLimitStatus.getRemaining();
		LOG.debug("Requests Remaining:"+searchRequestsRemaining);
		
		long searchResetTime = ((long)rateLimitStatus.getResetTimeInSeconds())*1000;
		LOG.debug("Reset Time:"+new Date(searchResetTime));
		
		long msecsRemaining = searchResetTime-now;
		LOG.debug("Remaining Time(ms):"+msecsRemaining+" ");
		
		long nextHomeRequest;
		if (searchRequestsRemaining>1) {
			long msPerRequest = msecsRemaining/(searchRequestsRemaining-1);
			nextHomeRequest = now+msPerRequest;
		} else {
			nextHomeRequest=searchResetTime+1000;
		}
		
		LOG.debug("Next Request: "+new Date(nextHomeRequest));
		
		return nextHomeRequest;
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
//	private static void sleepUntilNextAllowedSearchRequest(Connection<Twitter> conn) {
//		sleepUntil(nextSearchRequest.containsKey(conn)?nextSearchRequest.get(conn):0);
//	}
	private static void sleepUntilNextAllowedHomeRequest(Connection<Twitter> conn) {
		sleepUntil(nextHomeRequest.containsKey(conn)?nextHomeRequest.get(conn):0);
	}	
//	private static void sleepUntilNextAllowedShowRequest(Connection<Twitter> conn) {
//		sleepUntil(nextShowRequest.containsKey(conn)?nextShowRequest.get(conn):0);
//	}	

}
