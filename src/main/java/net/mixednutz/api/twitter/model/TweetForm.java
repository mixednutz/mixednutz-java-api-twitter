package net.mixednutz.api.twitter.model;

import net.mixednutz.api.model.IPost;
import twitter4j.StatusUpdate;

public class TweetForm implements IPost {
	
	private static final int MAX_TEXT_SIZE = 110;
	
	String status;
	String inReplyToStatusId;
	
	//Tweet Builder
	String textPart;
	String urlPart;
	String[] tagsPart;
	
	public TweetForm() {
		super();
	}

	public TweetForm(String status) {
		super();
		this.status = status;
	}

	public StatusUpdate toStatusUpdate() {
		StatusUpdate statusUpdate;
		if (status!=null) {
			statusUpdate = new StatusUpdate(status);
		} else if (textPart!=null && urlPart==null) {
			statusUpdate = new StatusUpdate(textPart);
		} else {
			//Shorten text
			StringBuffer buffer = new StringBuffer();
			if (textPart.length()>MAX_TEXT_SIZE) {
				buffer.append(textPart.substring(0, MAX_TEXT_SIZE-3))
					.append("...");
			} else {
				buffer.append(textPart);
			}
			buffer.append(" ").append(urlPart);
			if (tagsPart!=null) {
				for (String tag: tagsPart) {
					if (tag.startsWith("#")) {
						buffer.append(" ").append(tag);
					} else {
						buffer.append(" #").append(tag);
					}
				}
			}
			statusUpdate = new StatusUpdate(buffer.toString());
		}
		if (inReplyToStatusId!=null) {
			statusUpdate.setInReplyToStatusId(Long.parseLong(inReplyToStatusId));
		}
		
		return statusUpdate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setComposeBody(String status) {
		setStatus(status);
	}

	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	@Override
	public void setText(String text) {
		this.textPart = text;
		
	}

	@Override
	public void setUrl(String url) {
		this.urlPart = url;
	}

	public void setTags(String[] tags) {
		this.tagsPart = tags;
	}
	
}
