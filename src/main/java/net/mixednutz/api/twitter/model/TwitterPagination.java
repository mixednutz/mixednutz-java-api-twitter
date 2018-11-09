package net.mixednutz.api.twitter.model;

public class TwitterPagination {

	Integer pageSize;
	long sinceId;
	long maxId;
	
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public long getSinceId() {
		return sinceId;
	}
	public void setSinceId(long sinceId) {
		this.sinceId = sinceId;
	}
	public long getMaxId() {
		return maxId;
	}
	public void setMaxId(long maxId) {
		this.maxId = maxId;
	}
		
}
