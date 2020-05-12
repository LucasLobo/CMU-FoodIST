package com.grpc.server.model;

import com.google.protobuf.ByteString;

import java.util.ArrayList;

public class MenuItem {

	private final Integer id;
	private final String name;
	private final double price;
	private final String description;
	private final String foodType;
	private final ArrayList<ByteString> images = new ArrayList<>();

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

	public void addImage(ByteString image) {
		images.add(image);
	}

	public ArrayList<ByteString> getImages() {
		return images;
	}
}
