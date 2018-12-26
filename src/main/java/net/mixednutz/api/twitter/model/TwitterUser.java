package net.mixednutz.api.twitter.model;

import java.io.Serializable;
import java.util.List;

import net.mixednutz.api.core.model.Image;
import net.mixednutz.api.model.IAction;
import net.mixednutz.api.model.IImage;
import net.mixednutz.api.model.IUserSmall;
import twitter4j.User;

public class TwitterUser implements IUserSmall {
	
	User user;

	public TwitterUser(User user) {
		this.user = user;
	}

	@Override
	public String getUrl() {
		return user.getURL();
	}


	@Override
	public String getUri() {
		return null;
	}

	@Override
	public List<? extends IAction> getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getProviderId() {
		return user.getId();
	}

	@Override
	public String getUsername() {
		return "@"+user.getScreenName();
	}

	@Override
	public String getDisplayName() {
		return user.getName();
	}

	@Override
	public IImage getAvatar() {
		return new Image(user.getProfileImageURL(), getUsername()+"'s profile image");
	}

	@Override
	public boolean isPrivate() {
		return user.isProtected();
	}

}
