package com.grpc.server.model;



import com.grpc.server.util.LinearRegression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodService {

	private Integer id;

	private final HashMap<Integer,MenuItem> menuItems = new HashMap<>();
	private final HashMap<Integer, Integer> queue = new HashMap<>();
	private final LinearRegression lr = new LinearRegression();

	public FoodService(Integer id) {
		this.id = id;
	}

	public void addToQueue(Integer userId) {
		queue.put(userId, queue.size());
	}

	public void addToMenu(MenuItem item) {
		menuItems.put(item.getId(), item);
	}

	public void addImageToMenu(Integer menuId, Integer imageId) {
		menuItems.get(menuId).addImageId(imageId);
	}

	public List<MenuItem> getMenuItems() {
		return new ArrayList<>(menuItems.values());
	}

	public MenuItem getMenuItem(Integer menuItemId) {
		return menuItems.get(menuItemId);
	}

	public void removeFromQueue(Integer userId, Integer time) throws UserNotInQueueException {
		if (queue.get(userId) == null) throw new UserNotInQueueException(userId);
		lr.addDataPoint(queue.get(userId), time);
		queue.remove(userId);
	}

	public double estimateQueueTime() {
		return lr.predict(queue.size());
	}
}
