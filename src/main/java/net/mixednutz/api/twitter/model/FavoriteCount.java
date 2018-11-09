package net.mixednutz.api.twitter.model;

import net.mixednutz.api.model.ICount;

public class FavoriteCount implements ICount {
	
	Integer count;
	
	public FavoriteCount(Integer count) {
		super();
		this.count = count;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
}
