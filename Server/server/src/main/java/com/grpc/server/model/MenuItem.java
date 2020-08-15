package com.grpc.server.model;

import java.util.HashSet;
import java.util.Set;

public class MenuItem {

	private final Integer id;
	private final String name;
	private final double price;
	private final String description;
	private final String foodType;
	private final Set<Integer> imageIds = new HashSet<>();

	public MenuItem(Integer id, String name, double price, String description, String foodType) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.description = description;
		this.foodType = foodType;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public String getDescription() {
		return description;
	}

	public String getFoodType() {
		return foodType;
	}

	public void addImageId(Integer imageId) {
		imageIds.add(imageId);
	}

	public Set<Integer> getImageIds() {
		return imageIds;
	}
}
