package net.mixednutz.api.twitter.model;

import java.util.List;

import org.springframework.social.twitter.api.TwitterProfile;

import net.mixednutz.api.core.model.Image;
import net.mixednutz.api.model.IAction;
import net.mixednutz.api.model.IImage;
import net.mixednutz.api.model.IUserSmall;

public class TwitterUser implements IUserSmall {
	
	TwitterProfile twitterProfile;

	public TwitterUser(TwitterProfile twitterProfile) {
		this.twitterProfile = twitterProfile;
	}

	@Override
	public String getUrl() {
		return twitterProfile.getUrl();
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
	public String getUsername() {
		return twitterProfile.getScreenName();
	}

	@Override
	public String getDisplayName() {
		return twitterProfile.getName();
	}

	@Override
	public IImage getAvatar() {
		return new Image(twitterProfile.getProfileImageUrl(),getUsername()+"'s profile image");
	}

}
