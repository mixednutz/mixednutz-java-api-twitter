package net.mixednutz.api.twitter.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter4j.connect.TwitterConnectionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.mixednutz.api.core.model.Page;
import net.mixednutz.api.core.model.PageRequest;
import net.mixednutz.api.twitter.model.TweetElement;
import twitter4j.Twitter;

@Tag("IntegrationTest")
public class TwitterTimelineAdapterTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(TwitterTimelineAdapterTest.class);
	
	public static final String CONSUMER_KEY = "VoyzERnpC4MfjHm2Trp1mpC5L";
	public static final String CONSUMER_SECRET = "4kqQN8xhkgU5kRVGshfGvd51kUds7hlo8zSjVj7Dp1iwF5gXqm";
		
	private static final String ACCESS_ID = "228538942-7bjeI60YMfW5fVeAJLpRLeEPrOtXZUt4v3ENhiZm";
	private static final String ACCESS_SECRET = "YdHzmeIHQnoQLE3Bx5WIvX0Ubu6LGnvgB1f9oZRuhDCmQ";
	
	private TwitterTimelineAdapter timelineAdapter;
	
	@Disabled
	@Test
	public void testGetTimeline() {
		
		TwitterConnectionFactory connectionFactory= 
				new TwitterConnectionFactory(CONSUMER_KEY, CONSUMER_SECRET);
		
		OAuthToken oauthToken = new OAuthToken(ACCESS_ID, ACCESS_SECRET);
		Connection<Twitter> conn = connectionFactory.createConnection(oauthToken);
		
		timelineAdapter = new TwitterTimelineAdapter(conn);
				
		Page<TweetElement, Long> page = timelineAdapter.getTimeline();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		try {
			System.out.println(mapper.writeValueAsString(page));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//NEXT PAGE
		page = timelineAdapter.getTimeline(page.getNextPage());
		
		if (LOG.isDebugEnabled()) {
			try {
				LOG.debug(mapper.writeValueAsString(page));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		//PREV PAGE
		System.out.println(page.getReversePage());
		page = timelineAdapter.getTimeline(page.getReversePage());
		
		if (LOG.isDebugEnabled()) {
			try {
				LOG.debug(mapper.writeValueAsString(page));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	@Disabled
	@Test
	public void testPoll() {
		
		TwitterConnectionFactory connectionFactory= 
				new TwitterConnectionFactory(CONSUMER_KEY, CONSUMER_SECRET);
		
		OAuthToken oauthToken = new OAuthToken(ACCESS_ID, ACCESS_SECRET);
		Connection<Twitter> conn = connectionFactory.createConnection(oauthToken);
		
		timelineAdapter = new TwitterTimelineAdapter(conn);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
						
		//FIRST POLL PAGE
		Page<TweetElement, Long> page = timelineAdapter.getTimeline(
				timelineAdapter.getTimelinePollRequest(null));
		System.out.println("Found "+page.getItems().size()+" tweets");
		if (LOG.isDebugEnabled()) {
			try {
				LOG.trace(mapper.writeValueAsString(page));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//NEXT PREV PAGE
		System.out.println("Next: "+page.getNextPage());
		System.out.println("Rev:"+page.getReversePage());
		page = timelineAdapter.getTimeline(page.getNextPage());
		System.out.println("Found "+page.getItems().size()+" tweets");
		if (LOG.isDebugEnabled()) {
			try {
				LOG.trace(mapper.writeValueAsString(page));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (page.hasNext()) {
			//NEXT PREV PAGE
			System.out.println("Next: "+page.getNextPage());
			System.out.println("Rev:"+page.getReversePage());
			page = timelineAdapter.getTimeline(page.getNextPage());
			System.out.println("Found "+page.getItems().size()+" tweets");		
			if (LOG.isDebugEnabled()) {
				try {
					LOG.trace(mapper.writeValueAsString(page));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

	}
	
	@Disabled
	@Test
	public void testGetUserTimeline() {
		
		TwitterConnectionFactory connectionFactory= 
				new TwitterConnectionFactory(CONSUMER_KEY, CONSUMER_SECRET);
		
		OAuthToken oauthToken = new OAuthToken(ACCESS_ID, ACCESS_SECRET);
		Connection<Twitter> conn = connectionFactory.createConnection(oauthToken);
		
		timelineAdapter = new TwitterTimelineAdapter(conn);
				
		Page<TweetElement, Long> page = timelineAdapter.getUserTimeline();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		try {
			System.out.println(mapper.writeValueAsString(page));
			System.out.println("Prev (Newer):");
			System.out.println(page.getReversePage());
			System.out.println("Next (Older):");
			System.out.println(page.getNextPage());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PageRequest<Long> prevPage = page.getReversePage();
		
		//NEXT PAGE
		System.out.println("Next (Older than):");
		System.out.println(page.getNextPage());
		page = timelineAdapter.getUserTimeline(page.getNextPage());
		assertFalse(page.getItems().isEmpty());
		try {
			System.out.println(mapper.writeValueAsString(page));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//PREV PAGE
		System.out.println("Prev (Newer than /should be empty because only milliseconds have passed):");
		System.out.println(prevPage);
		page = timelineAdapter.getUserTimeline(prevPage);
		assertTrue(page.getItems().isEmpty());
		try {
			System.out.println(mapper.writeValueAsString(page));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
