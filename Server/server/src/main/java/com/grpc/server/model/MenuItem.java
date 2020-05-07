package com.grpc.server.model;

public class MenuItem {

	private final String name;
	private final double price;
	private final String description;
	private final String foodType;

	public MenuItem(String name, double price, String description, String foodType) {
		this.name = name;
		this.price = price;
		this.description = description;
		this.foodType = foodType;
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
}
