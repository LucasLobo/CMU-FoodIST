package com.grpc.server;

import com.grpc.Contract.*;
import com.grpc.GrpcServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;

public class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase {

    public static GrpcServiceImpl instance = null;
    private HashMap<String, User> usersMap = new HashMap<String, User>(); //username, User
    private ArrayList<String> menuItems = new ArrayList<String>();

    public static GrpcServiceImpl getInstance() {

        if (instance == null) {

            instance = new GrpcServiceImpl();
        }
        return instance;
    }

    private HashMap<String, User> getUsersMap() {
        return usersMap;
    }

    private User getUser(String username) {
        return usersMap.get(username);
    }

    @Override
    public synchronized void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        System.out.println("Register Request Received: " + request);

        String username = request.getUsername();
        String password = request.getPassword();

        User user = new User();
        user.setPassword(password);
        usersMap.put(username, user);

		RegisterResponse response = RegisterResponse.newBuilder()
                    .setResult("Hello " + username).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveProfile(ProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        System.out.println("Profile Request Received: " + request);

        String username = request.getUsername();
        String status = request.getStatus();
        String constraints = request.getConstraints();

        User user = usersMap.get(username);
        user.setStatus(status);
        user.setConstraints(constraints);

        ProfileResponse response = ProfileResponse.newBuilder()
                .setResult("Saved " + username + "; Status: " + status + "; Constraints: " + constraints).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveMenuItem(ItemRequest request, StreamObserver<ItemResponse> responseObserver) {
        System.out.println("Save Menu Request Received: " + request);

        String item = request.getItem();

        menuItems.add(item);

        ItemResponse response = ItemResponse.newBuilder()
                .setResult("Saved " + item).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
