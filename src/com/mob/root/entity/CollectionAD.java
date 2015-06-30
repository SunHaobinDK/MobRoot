package com.mob.root.entity;

import java.util.List;

public class CollectionAD {

	private List<AD> ads;
	private String hotGames;
	private String weekGames;
	private boolean showFlavors;
	private List<Flavor> flavors;

	public List<AD> getAds() {
		return ads;
	}

	public void setAds(List<AD> ads) {
		this.ads = ads;
	}

	public String getHotGames() {
		return hotGames;
	}

	public void setHotGames(String hotGames) {
		this.hotGames = hotGames;
	}

	public String getWeekGames() {
		return weekGames;
	}

	public void setWeekGames(String weekGames) {
		this.weekGames = weekGames;
	}

	public boolean isShowFlavors() {
		return showFlavors;
	}

	public void setShowFlavors(boolean showFlavors) {
		this.showFlavors = showFlavors;
	}

	public List<Flavor> getFlavors() {
		return flavors;
	}

	public void setFlavors(List<Flavor> flavors) {
		this.flavors = flavors;
	}
}
