package com.grpc.server;

public class UserNotInQueueException extends RuntimeException {

	UserNotInQueueException(Integer user) {
		super("User not in Queue Exception");
	}
}
