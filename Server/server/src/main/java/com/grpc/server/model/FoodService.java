package com.grpc.server.model;



import com.grpc.server.util.LinearRegression;

import java.util.HashMap;

public class FoodService {

	private Integer id;

	private final HashMap<Integer, Integer> queue = new HashMap<>();
	private final LinearRegression lr = new LinearRegression();

	public FoodService(Integer id) {
		this.id = id;
	}

	public void addToQueue(Integer userId) {
		queue.put(userId, queue.size());
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
