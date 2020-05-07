package com.grpc.server.model;

public class UserNotInQueueException extends RuntimeException {

	UserNotInQueueException(Integer userId) {
		super("User '" + userId + "' not in Queue Exception");
	}
}
