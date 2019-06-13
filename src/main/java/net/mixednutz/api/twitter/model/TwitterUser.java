package net.mixednutz.api.twitter.model;

import net.mixednutz.api.core.model.Image;
import net.mixednutz.api.core.model.UserSmall;
import twitter4j.User;

public class TwitterUser extends UserSmall {
		
	public TwitterUser() {
		super();
	}

	public TwitterUser(User user) {
		this.setProviderId(Long.toString(user.getId()));
		this.setUrl("https://twitter.com/"+user.getScreenName());
		this.setUri("/users/show.json?screen_name="+user.getScreenName());
		this.setUsername("@"+user.getScreenName());
		this.setDisplayName(user.getName());
		this.setAvatar(new Image(user.getProfileImageURL(), getUsername()+"'s profile image"));
		this.setPrivate(user.isProtected());
	}

}
