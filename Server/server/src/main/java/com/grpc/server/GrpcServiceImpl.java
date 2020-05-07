package com.grpc.server;

import com.google.protobuf.ByteString;
import com.grpc.Contract.*;
import com.grpc.GrpcServiceGrpc;
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
		OK, USERNAME_DOES_NOT_EXIST, INCORRECT_PASSWORD
	}


    public static GrpcServiceImpl instance = null;
    private final HashMap<String, User> usersMap = new HashMap<>(); //username, User
    private final HashMap<Integer, FoodService> foodServicesMap = new HashMap<>();
    private final ArrayList<String> menuItems = new ArrayList<>();
    private final AtomicInteger userQueueId = new AtomicInteger(0);

    public static GrpcServiceImpl getInstance() {
        if (instance == null) {
            instance = new GrpcServiceImpl();
        }
        return instance;
    }

    private GrpcServiceImpl() {
        for (int id = 0; id < 17; id++) {
            foodServicesMap.put(id, new FoodService(id));
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
            usersMap.put(username, user);
            code = AuthMessage.OK.name();
        }

        Signature signature = createSignature();
        RegisterResponse response = RegisterResponse.newBuilder().setResult(code).setSignature(signature).build();
        System.out.println(code);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveProfile(SaveProfileRequest request, StreamObserver<SaveProfileResponse> responseObserver) {
        System.out.println("Profile Request Received:\n" + request);

        AuthMessage authMessage = auth(request.getAuth());

		String username = request.getAuth().getUsername().toLowerCase();
        User user = usersMap.get(username);

        String status = request.getStatus();
        List<String> constraints = request.getConstraintsList();

        String code = authMessage.name();
        if (authMessage.equals(AuthMessage.OK)) {
            user.setStatus(status);
            user.setConstraints(constraints);
        }

        Signature signature = createSignature();
        SaveProfileResponse response = SaveProfileResponse.newBuilder().setResult(code).setSignature(signature).build();
        System.out.println(code);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void fetchProfile(FetchProfileRequest request, StreamObserver<FetchProfileResponse> responseObserver) {
        System.out.println("Fetch Profile Request Received:\n" + request);

        AuthMessage authMessage = auth(request.getAuth());

		String username = request.getAuth().getUsername().toLowerCase();
        User user = usersMap.get(username);

        FetchProfileResponse.Builder builder = FetchProfileResponse.newBuilder();

        String code = authMessage.name();
        if (authMessage.equals(AuthMessage.OK)) {
            List<String> constraints = user.getConstraints();
            builder.addAllConstraints(constraints);
            builder.setStatus(user.getStatus());

            System.out.println(user.getStatus());
            System.out.println(user.getConstraints());
        }

        Signature signature = createSignature();

        System.out.println(code);
        builder.setResult(code).setSignature(signature);
        FetchProfileResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveMenuItem(ItemRequest request, StreamObserver<ItemResponse> responseObserver) {
        System.out.println("Save Menu Request Received:\n" + request);

        String item = request.getItem();

        menuItems.add(item);

        Signature signature = createSignature();
        ItemResponse response = ItemResponse.newBuilder()
                .setResult("Saved " + item).setSignature(signature).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void joinQueue(JoinQueueRequest request, StreamObserver<JoinQueueResponse> responseObserver) {
        System.out.println("Join Queue Request Received:\n" + request);

        int foodServiceId = request.getFoodServiceId();
        FoodService foodService = foodServicesMap.get(foodServiceId);

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
        FoodService foodService = foodServicesMap.get(foodServiceId);
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

        FoodService foodService = foodServicesMap.get(foodServiceId);
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

        User user = usersMap.get(username);
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
