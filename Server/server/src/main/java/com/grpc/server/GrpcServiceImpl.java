package com.grpc.server;

import com.grpc.Contract;
import com.grpc.Contract.*;
import com.grpc.GrpcServiceGrpc;

import com.grpc.server.model.MenuItem;
import com.grpc.server.model.FoodService;
import com.grpc.server.model.User;
import com.grpc.server.model.UserNotInQueueException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase {

	private enum AuthMessage {
		OK, USERNAME_TAKEN, USERNAME_DOES_NOT_EXIST, INCORRECT_PASSWORD
	}


    public static GrpcServiceImpl instance = null;
    private final HashMap<String, User> users = new HashMap<>(); //username, User
    private final HashMap<Integer, FoodService> foodServices = new HashMap<>();
    private final AtomicInteger userQueueId = new AtomicInteger(0);

    public static GrpcServiceImpl getInstance() {
        if (instance == null) {
            instance = new GrpcServiceImpl();
        }
        return instance;
    }

    private GrpcServiceImpl() {
        for (int id = 0; id < 17; id++) {
            foodServices.put(id, new FoodService(id));
        }
    }

    @Override
    public synchronized void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        System.out.println("Register Request Received:\n" + request);

        AuthMessage authMessage = auth(request.getAuth());

        String username = request.getAuth().getUsername().toLowerCase();
        String password = request.getAuth().getPassword();

        String code = authMessage.name();
        if (authMessage.equals(AuthMessage.USERNAME_DOES_NOT_EXIST)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            users.put(username, user);
            code = AuthMessage.OK.name();
        } else {
            code = AuthMessage.USERNAME_TAKEN.name();
        }

        Signature signature = createSignature();
        RegisterResponse response = RegisterResponse.newBuilder().setCode(code).setSignature(signature).build();
        System.out.println(code);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveProfile(SaveProfileRequest request, StreamObserver<SaveProfileResponse> responseObserver) {
        System.out.println("Profile Request Received:\n" + request);

        AuthMessage authMessage = auth(request.getAuth());

		String username = request.getAuth().getUsername().toLowerCase();
        User user = users.get(username);

        String status = request.getProfile().getStatus();
        List<String> constraints = request.getProfile().getConstraintsList();

        String code = authMessage.name();
        if (authMessage.equals(AuthMessage.OK)) {
            user.setStatus(status);
            user.setConstraints(constraints);
        }

        Signature signature = createSignature();
        SaveProfileResponse response = SaveProfileResponse.newBuilder().setCode(code).setSignature(signature).build();
        System.out.println(code);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void fetchProfile(FetchProfileRequest request, StreamObserver<FetchProfileResponse> responseObserver) {
        System.out.println("Fetch Profile Request Received:\n" + request);

        AuthMessage authMessage = auth(request.getAuth());

		String username = request.getAuth().getUsername().toLowerCase();
        User user = users.get(username);

        Profile.Builder profileBuilder = Profile.newBuilder();

        String code = authMessage.name();
        if (authMessage.equals(AuthMessage.OK)) {
            List<String> constraints = user.getConstraints();
            profileBuilder.addAllConstraints(constraints);
            profileBuilder.setStatus(user.getStatus());

            System.out.println(user.getStatus());
            System.out.println(user.getConstraints());
        }

        Signature signature = createSignature();

        System.out.println(code);

        FetchProfileResponse response = FetchProfileResponse.newBuilder()
                .setCode(code)
                .setSignature(signature)
                .setProfile(profileBuilder.build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveMenuItem(SaveMenuItemRequest request, StreamObserver<SaveMenuItemResponse> responseObserver) {
        System.out.println("Save Menu Request Received:\n" + request);

        Integer foodServiceId = request.getFoodServiceId();
        Contract.MenuItem item = request.getMenuItem();

        FoodService foodService;
        String code;
        try {
            foodService = foodServices.get(foodServiceId);
            MenuItem menuItem = new MenuItem(item.getName(), item.getPrice(), item.getDescription(), item.getFoodType());
            foodService.addToMenu(menuItem);
            code = "OK";
        } catch (NullPointerException e) {
            code = "FOOD_SERVICE_NOT_FOUND";
        }

        Signature signature = createSignature();
        SaveMenuItemResponse response = SaveMenuItemResponse.newBuilder()
                .setCode(code).setSignature(signature).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void fetchMenus(FetchMenusRequest request, StreamObserver<FetchMenusResponse> responseObserver) {
        System.out.println("Fetch Menu Request Received:\n" + request);

        int foodServiceId = request.getFoodServiceId();

        FetchMenusResponse.Builder responseBuilder = FetchMenusResponse.newBuilder();
        try {
            FoodService foodService = foodServices.get(foodServiceId);
            List<MenuItem> menuItems = foodService.getMenuItems();
            for (MenuItem menuItem : menuItems) {
                Contract.MenuItem item = Contract.MenuItem.newBuilder()
                        .setName(menuItem.getName())
                        .setDescription(menuItem.getDescription())
                        .setFoodType(menuItem.getFoodType())
                        .setPrice(menuItem.getPrice())
                        .build();
                responseBuilder.addMenuItems(item);
            }

        } catch (NullPointerException ignored) {
        }

        Signature signature = createSignature();
        responseBuilder.setSignature(signature);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void joinQueue(JoinQueueRequest request, StreamObserver<JoinQueueResponse> responseObserver) {
        System.out.println("Join Queue Request Received:\n" + request);

        int foodServiceId = request.getFoodServiceId();
        FoodService foodService = foodServices.get(foodServiceId);

        int userQueueId;
        if (foodService == null) {
            userQueueId = -1;
        } else {
            userQueueId = this.userQueueId.getAndIncrement();
            foodService.addToQueue(userQueueId);
        }

        Signature signature = createSignature();
        JoinQueueResponse response = JoinQueueResponse.newBuilder().setUserQueueId(userQueueId).setSignature(signature).build();
        System.out.println("User queue id: " + userQueueId);
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void leaveQueue(LeaveQueueRequest request, StreamObserver<LeaveQueueResponse> responseObserver) {
        System.out.println("Leave Queue Request Received:\n" + request);

        int duration = request.getDuration();
        int foodServiceId = request.getFoodServiceId();
        int userQueueId = request.getUserQueueId();

        String code;
        FoodService foodService = foodServices.get(foodServiceId);
        LeaveQueueResponse response;
        try {
            foodService.removeFromQueue(userQueueId, duration);
            System.out.println("UserId: " + userQueueId + " duration:" + duration);
            code = "OK";
        } catch (UserNotInQueueException e) {
            code = "USER_NOT_IN_QUEUE";
        } catch (NullPointerException e) {
            code = "FOOD_SERVICE_NOT_FOUND";
        }

        Signature signature = createSignature();
        response = LeaveQueueResponse.newBuilder().setResult(code).setSignature(signature).build();
        System.out.println(code);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void estimateQueueTime(EstimateQueueTimeRequest request, StreamObserver<EstimateQueueTimeResponse> responseObserver) {
        System.out.println("Estimate Queue Request Received:\n" + request);

        int foodServiceId = request.getFoodServiceId();

        FoodService foodService = foodServices.get(foodServiceId);
        int queueTime;
        try {
            queueTime = (int) Math.round(foodService.estimateQueueTime());
            System.out.println("QueueTime:" + queueTime);
        } catch (NullPointerException e) {
            queueTime = -1;
        }

        Signature signature = createSignature();
        EstimateQueueTimeResponse response = EstimateQueueTimeResponse.newBuilder().setTime(queueTime).setSignature(signature).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private AuthMessage auth(Auth auth) {
        String username = auth.getUsername().toLowerCase();
        String password = auth.getPassword();

        User user = users.get(username);
        if (user == null) {
            return AuthMessage.USERNAME_DOES_NOT_EXIST;
        } else if (!user.passwordMatch(password)) {
            return AuthMessage.INCORRECT_PASSWORD;
        } else {
            return AuthMessage.OK;
        }
	}

	private Signature createSignature() {
//        byte[] hash = DigitalSign.getHashFromObject(result);
//        byte[] sigServer = DigitalSign.getSignature(hash, "server");
//        ByteString signature = ByteString.copyFrom(sigServer);

        return Signature.newBuilder()/*.setSignature()*/.build();
    }



}
