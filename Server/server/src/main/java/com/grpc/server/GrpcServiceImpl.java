package com.grpc.server;

import com.grpc.Contract.*;
import com.grpc.GrpcServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;

public class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase {

    public static GrpcServiceImpl instance = null;
    private HashMap<String, User> usersMap = new HashMap<>(); //username, User
    private HashMap<Integer, FoodService> foodServicesMap = new HashMap<>();

    private ArrayList<String> menuItems = new ArrayList<>();

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

    @Override
    public void joinQueue(JoinQueueRequest request, StreamObserver<JoinQueueResponse> responseObserver) {
        System.out.println("Join Queue Request Received: " + request);
        int foodServiceId = request.getFoodServiceId();
        int userId = request.getUserId();

        FoodService foodService = foodServicesMap.get(foodServiceId);
        if (foodService == null) {
            foodService = new FoodService(foodServiceId);
            foodServicesMap.put(foodServiceId, foodService);
        }

        foodService.addToQueue(userId);

        JoinQueueResponse response = JoinQueueResponse.newBuilder().setResult("Added to queue").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void leaveQueue(LeaveQueueRequest request, StreamObserver<LeaveQueueResponse> responseObserver) {
        System.out.println("Leave Queue Request Received: " + request);

        int duration = request.getDuration();
        int foodServiceId = request.getFoodServiceId();
        int userId = request.getUserId();

        FoodService foodService = foodServicesMap.get(foodServiceId);
        LeaveQueueResponse response;
        try {
            foodService.removeFromQueue(userId, duration);
            response = LeaveQueueResponse.newBuilder().setResult("OK").build();
        } catch (UserNotInQueueException e) {
            response = LeaveQueueResponse.newBuilder().setResult("USER_NOT_IN_QUEUE").build();
        } catch (NullPointerException e) {
            response = LeaveQueueResponse.newBuilder().setResult("FOOD_SERVICE_NOT_FOUND").build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void estimateQueueTime(EstimateQueueTimeRequest request, StreamObserver<EstimateQueueTimeResponse> responseObserver) {
        System.out.println("Estimate Queue Request Received: " + request);

        int foodServiceId = request.getFoodServiceId();

        FoodService foodService = foodServicesMap.get(foodServiceId);
        EstimateQueueTimeResponse response;
        try {
            double queueTime = foodService.estimateQueueTime();
            System.out.println("QueueTime:" + queueTime);
            response = EstimateQueueTimeResponse.newBuilder().setTime((int) Math.round(queueTime)).build();
        } catch (NullPointerException e) {
            response = EstimateQueueTimeResponse.newBuilder().setTime(-1).build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
