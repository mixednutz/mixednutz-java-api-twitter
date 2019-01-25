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
import net.mixednutz.api.client.UserClient;
import net.mixednutz.api.core.model.Page;
import net.mixednutz.api.core.model.PageRequest;
import net.mixednutz.api.model.IPage;
import net.mixednutz.api.model.IPageRequest;
import net.mixednutz.api.model.IPageRequest.Direction;
import net.mixednutz.api.model.ITimelineElement;
import net.mixednutz.api.model.IUserSmall;
import net.mixednutz.api.twitter.model.TweetElement;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class TwitterTimelineAdapter implements TimelineClient<Long>, UserClient<Long> {
	
	private static final Log LOG = LogFactory.getLog(TwitterTimelineAdapter.class);
	
//	private static Map<Connection<Twitter>, Long> nextSearchRequest=
//			new HashMap<Connection<Twitter>, Long>();
	private static Map<Connection<Twitter>, Long> nextHomeTimelineRequest=
			new HashMap<Connection<Twitter>, Long>();
//	private static Map<Connection<Twitter>, Long> nextShowRequest=
//			new HashMap<Connection<Twitter>, Long>();
	private static Map<Connection<Twitter>, Long> nextUserTimelineRequest=
			new HashMap<Connection<Twitter>, Long>();
	
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
	
	protected PageRequest<Long> parseStringPaginationToken(IPageRequest<String> pagination) {
		if (pagination.getStart()!=null) {
			return PageRequest.next(
					Long.valueOf(pagination.getStart()),
					pagination.getPageSize(),
					pagination.getDirection());
		} 
		return PageRequest.first(pagination.getPageSize(), pagination.getDirection(), Long.class);
	}

	@Override
	public <T> IPageRequest<T> getTimelinePollRequest(T start) {
		// Get tweets from starting time.  Limit 200.
		return PageRequest.next(start, 200, Direction.GREATER_THAN);
	}

	@Override
	public Page<TweetElement, Long> getTimeline() {
		return getTimeline(null);
	}
	
	@Override
	public IPage<? extends ITimelineElement, Long> getTimelineStringToken(IPageRequest<String> pagination) {
		return getTimeline(parseStringPaginationToken(pagination));
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
				sleepUntilNextAllowedHomeTimelineRequest(conn);
			}
			
			response = conn.getApi().getHomeTimeline(paging);
			updateHomeTimelineRateLimit(conn, response.getRateLimitStatus());
			
			List<Status> results = new ArrayList<Status>();
			for (Status status: response) {
				//TODO do filter for hashtag
				results.add(status);
			}
			
			//Remember when we added +5? Let's
			//trim to ensure results match original page count
			if (!results.isEmpty() && 
					pagination!=null && pagination.getPageSize()>0) {
				if (results.size()>pagination.getPageSize()) {
					results = results.subList(0, pagination.getPageSize());
				}
				paging.setCount(pagination.getPageSize());
			}
						
			//Wrap in Page
			return toPage(results, paging, pagination);
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IPage<? extends ITimelineElement, Long> getPublicTimeline() {
		return getPublicTimeline(null);
	}
	
	@Override
	public IPage<? extends ITimelineElement, Long> getPublicTimelineStringToken(IPageRequest<String> pagination) {
		return getPublicTimeline(parseStringPaginationToken(pagination));
	}

	@Override
	public IPage<? extends ITimelineElement, Long> getPublicTimeline(IPageRequest<Long> pagination) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IUserSmall getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IUserSmall getUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <T> IPageRequest<T> getUserTimelinePollRequest(T start) {
		// Get tweets from starting time.  Limit 200.
		return getTimelinePollRequest(start);
	}

	@Override
	public Page<TweetElement, Long> getUserTimeline() {
		return getUserTimeline((IPageRequest<Long>)null);
	}
	
	@Override
	public IPage<? extends ITimelineElement, Long> getUserTimelineStringToken(IPageRequest<String> pagination) {
		return getUserTimeline(parseStringPaginationToken(pagination));
	}

	@Override
	public Page<TweetElement, Long> getUserTimeline(IPageRequest<Long> pagination) {
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
				sleepUntilNextAllowedUserTimelineRequest(conn);
			}
			
			response = conn.getApi().getUserTimeline(paging);
			updateUserTimelineRateLimit(conn, response.getRateLimitStatus());
			
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
			return toPage(results, paging, pagination);
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IPage<? extends ITimelineElement, Long> getUserTimeline(String username) {
		return getUserTimeline(username, null);
	}
	
	@Override
	public IPage<? extends ITimelineElement, Long> getUserTimelineStringToken(String username,
			IPageRequest<String> pagination) {
		return getUserTimeline(username, parseStringPaginationToken(pagination));
	}

	@Override
	public IPage<? extends ITimelineElement, Long> getUserTimeline(String username, IPageRequest<Long> pagination) {
		throw new UnsupportedOperationException("Twitter does not allow unauthenticated timeline queries, so this isn't implemented");
	}

	@Override
	public void subscribeToUser(String username) {
		// TODO Auto-generated method stub
		
	}

	Paging toTwitterPaging(IPageRequest<Long> pageRequest) {
		Paging paging = new Paging();
		if (pageRequest!=null) {
			paging.setCount(pageRequest.getPageSize());
			
			if (pageRequest.getDirection()==Direction.LESS_THAN){
				if (pageRequest.getStart()!=null) {
					paging.setMaxId(pageRequest.getStart());
				}

			} else {
				if (pageRequest.getStart()!=null) {
					paging.setSinceId(pageRequest.getStart());
				}
			}
		}
				
		return paging;
	}
	
	PageRequest<Long> toPageRequest(Integer pageSize, Long maxId, Long sinceId) {
		if (maxId!=null) {
			return PageRequest.next(maxId, pageSize, Direction.LESS_THAN);
		}
		if (sinceId!=null) {
			return PageRequest.next(sinceId, pageSize, Direction.GREATER_THAN);
		}
		return null;
	}
	
	PageRequest<Long> toPageRequest(IPageRequest<Long> pageRequest) {
		return PageRequest.next(pageRequest.getStart(), pageRequest.getPageSize(), 
				pageRequest.getDirection());
	}
	
	Page<TweetElement, Long> toPage(List<Status> items, Paging prevPage, IPageRequest<Long> pageRequest) {
		LinkedList<TweetElement> newItemList = new LinkedList<>();
		for (Status item: items) {
			newItemList.add(new TweetElement(item));
		}
				
		Page<TweetElement, Long> newPage = new Page<>();
		newPage.setItems(newItemList);
		if (pageRequest!=null) {
			newPage.setPageRequest(toPageRequest(pageRequest));
		}
		if (!items.isEmpty()) {
			int pageSize =prevPage!=null&&prevPage.getCount()>0?prevPage.getCount():items.size();
			Long last = Long.valueOf(newItemList.getLast().getPaginationId());
			Long first = Long.valueOf(newItemList.getFirst().getPaginationId());
			if ((pageRequest!=null && Direction.GREATER_THAN.equals(pageRequest.getDirection())) ||
					(prevPage.getMaxId()<1 && prevPage.getSinceId()>=1)) {
				newPage.setNextPage(toPageRequest(pageSize, null, first));
				newPage.setReversePage(toPageRequest(pageSize, last, null));
			} else {
				newPage.setNextPage(toPageRequest(pageSize, last, null));
				newPage.setReversePage(toPageRequest(pageSize, null, first));
			} 
			newPage.setHasNext(true);
			newPage.setHasReverse(true);
		}
		return newPage;
	}
	
//	private static synchronized void updateSearchRateLimit(Connection<Twitter> conn, 
//			RateLimitStatus rateLimitStatus) {
//		nextSearchRequest.put(conn, getNextRequest(rateLimitStatus));
//	}
	
	private static synchronized void updateHomeTimelineRateLimit(Connection<Twitter> conn, 
			RateLimitStatus rateLimitStatus) {
		nextHomeTimelineRequest.put(conn, getNextRequest(rateLimitStatus));
	}
	
//	private static synchronized void updateShowRateLimit(Connection<Twitter> conn, 
//			RateLimitStatus rateLimitStatus) {
//		nextShowRequest.put(conn, getNextRequest(rateLimitStatus));
//	}
	
	private static synchronized void updateUserTimelineRateLimit(Connection<Twitter> conn, 
			RateLimitStatus rateLimitStatus) {
		nextUserTimelineRequest.put(conn, getNextRequest(rateLimitStatus));
	}
	
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
	private static void sleepUntilNextAllowedHomeTimelineRequest(Connection<Twitter> conn) {
		sleepUntil(nextHomeTimelineRequest.containsKey(conn)?nextHomeTimelineRequest.get(conn):0);
	}	
//	private static void sleepUntilNextAllowedShowRequest(Connection<Twitter> conn) {
//		sleepUntil(nextShowRequest.containsKey(conn)?nextShowRequest.get(conn):0);
//	}	
	private static void sleepUntilNextAllowedUserTimelineRequest(Connection<Twitter> conn) {
		sleepUntil(nextUserTimelineRequest.containsKey(conn)?nextUserTimelineRequest.get(conn):0);
	}

}
