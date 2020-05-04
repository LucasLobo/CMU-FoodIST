package com.grpc.server;



import com.grpc.server.util.LinearRegression;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodService {

	private Integer id;

	private final HashMap<Integer, Integer> queue = new HashMap<>();
	private final LinearRegression lr = new LinearRegression();


	public FoodService(Integer id) {
		this.id = id;
	}

	public void addToQueue(Integer id) {
		queue.put(id, queue.size());
	}

	public void removeFromQueue(Integer id, Integer time) throws UserNotInQueueException {
		if (queue.get(id) == null) throw new UserNotInQueueException(id);
		lr.addDataPoint(queue.get(id), time);
		queue.remove(id);
	}

	public double estimateQueueTime() {
		return lr.predict(queue.size());
	}

	public static void main(String[] args) {
		FoodService foodService = new FoodService(0);

		foodService.addToQueue(0);
		foodService.removeFromQueue(0,0);

		foodService.addToQueue(1);
		foodService.removeFromQueue(1,10);

		foodService.addToQueue(2);
		foodService.removeFromQueue(2,10);


		System.out.println(foodService.estimateQueueTime());

	}

}
