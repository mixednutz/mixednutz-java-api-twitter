package net.mixednutz.api.twitter.model;

import net.mixednutz.api.model.IReshareCount;
import net.mixednutz.api.twitter.TwitterFeedType;

public class RetweetCount implements IReshareCount {
	
	Integer count;
	TwitterFeedType networkInfo;
	
	public RetweetCount(Integer count, TwitterFeedType networkInfo) {
		super();
		this.count = count;
		this.networkInfo = networkInfo;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public TwitterFeedType getNetworkInfo() {
		return networkInfo;
	}
	public void setNetworkInfo(TwitterFeedType networkInfo) {
		this.networkInfo = networkInfo;
	}

}
