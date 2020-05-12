package com.grpc.server;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.grpc.Contract;
import com.grpc.Contract.*;
import com.grpc.GrpcServiceGrpc;

import com.grpc.server.model.MenuItem;
import com.grpc.server.model.FoodService;
import com.grpc.server.model.User;
import com.grpc.server.model.UserNotInQueueException;
import com.grpc.server.util.Chunks;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final AtomicInteger menuId = new AtomicInteger(0);

    public static GrpcServiceImpl getInstance() {
        if (instance == null) {
            instance = new GrpcServiceImpl();
        }
        return instance;
    }

    private GrpcServiceImpl() {
        initData();
    }

    @Override
    public synchronized void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        System.out.println("Register Request Received:\n" + request);

        AuthMessage authMessage = auth(request.getAuth());

        String username = request.getAuth().getUsername().toLowerCase();
        String password = request.getAuth().getPassword();

        String code;
        if (authMessage.equals(AuthMessage.USERNAME_DOES_NOT_EXIST)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            users.put(username, user);
            code = AuthMessage.OK.name();
        } else {
            code = AuthMessage.USERNAME_TAKEN.name();
        }

        RegisterResponse response = RegisterResponse.newBuilder().setCode(code).build();
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

        SaveProfileResponse response = SaveProfileResponse.newBuilder().setCode(code).build();
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


        System.out.println(code);

        FetchProfileResponse response = FetchProfileResponse.newBuilder()
                .setCode(code)
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

        SaveMenuItemResponse.Builder responseBuilder = SaveMenuItemResponse.newBuilder();
        try {
            foodService = foodServices.get(foodServiceId);
            MenuItem menuItem = new MenuItem(menuId.getAndIncrement(), item.getName(), item.getPrice(), item.getDescription(), item.getFoodType());
            foodService.addToMenu(menuItem);
            responseBuilder.setMenuId(menuItem.getId());
            code = "OK";
        } catch (NullPointerException e) {
            code = "FOOD_SERVICE_NOT_FOUND";
        }

        SaveMenuItemResponse response = responseBuilder.setCode(code).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<SaveImageToMenuItemRequest> saveImage(StreamObserver<SaveImageToMenuItemResponse> responseObserver) {
        System.out.print("Save Image Request Received:");
        return new StreamObserver<SaveImageToMenuItemRequest>() {
            Integer foodServiceId;
            Integer menuItemId;
            Integer chunksAmount;
            final ArrayList<ByteString> image = new ArrayList<>();

            @Override
            public void onNext(SaveImageToMenuItemRequest request) {
                if (request.hasMetaData()) {
                    foodServiceId = request.getMetaData().getFoodServiceId();
                    menuItemId = request.getMetaData().getMenuItemId();
                    chunksAmount = request.getMetaData().getChunksAmount();
                } else {
                    int position = request.getChunk().getPosition();
                    ByteString chunk = request.getChunk().getData();
                    image.add(position, chunk);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                ByteString imageBytestring = ByteString.EMPTY;
                for (ByteString bytestring : image) {
                    imageBytestring = imageBytestring.concat(bytestring);
                }
                System.out.println("Chunks:" + chunksAmount);
                FoodService foodService = foodServices.get(foodServiceId);
                foodService.addImageToMenu(menuItemId, imageBytestring);
                SaveImageToMenuItemResponse response = SaveImageToMenuItemResponse.newBuilder().setCode("OK").build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void fetchImages(FetchImagesFromMenuRequest request, StreamObserver<FetchImagesFromMenuResponse> responseObserver) {
        System.out.println("Fetch Image Request Received:\n");

        int menuItemId = request.getMetadata().getMenuItemId();
        int foodServiceId = request.getMetadata().getFoodServiceId();

        MenuItem menuItem = foodServices.get(foodServiceId).getMenuItem(menuItemId);
        ArrayList<ByteString> images = menuItem.getImages();

        for (int imageIndex = 0; imageIndex < images.size(); imageIndex++) {
            ByteString image = images.get(imageIndex);
            byte[] byteArray = image.toByteArray();
            byte[][] chunkedByteArrays = Chunks.splitArray(byteArray, 1024);
            int numberOfChunks = chunkedByteArrays.length;

            FetchImagesFromMenuResponse response;
            for (int i = 0; i < numberOfChunks; i++) {
                ImageChunk chunk = ImageChunk.newBuilder().setImageIndex(imageIndex).setPosition(i).setData(ByteString.copyFrom(chunkedByteArrays[i])).build();
                response = FetchImagesFromMenuResponse.newBuilder().setChunk(chunk).build();
                responseObserver.onNext(response);
            }
        }
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
                        .setId(menuItem.getId())
                        .setName(menuItem.getName())
                        .setDescription(menuItem.getDescription())
                        .setFoodType(menuItem.getFoodType())
                        .setPrice(menuItem.getPrice())
                        .build();
                responseBuilder.addMenuItems(item);
            }

        } catch (NullPointerException ignored) {
        }

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

        JoinQueueResponse response = JoinQueueResponse.newBuilder().setUserQueueId(userQueueId).build();
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

        response = LeaveQueueResponse.newBuilder().setResult(code).build();
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

        EstimateQueueTimeResponse response = EstimateQueueTimeResponse.newBuilder().setTime(queueTime).build();
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

	private void initData() {
        for (int id = 0; id < 17; id++) {
            foodServices.put(id, new FoodService(id));
        }

        FoodService foodService = foodServices.get(0);
        MenuItem menuItem = new MenuItem(menuId.getAndIncrement(), "Meat Menu", 5, "Menu with Meat", "MEAT");
        foodService.addToMenu(menuItem);

        menuItem = new MenuItem(menuId.getAndIncrement(), "Fish Menu", 8, "Menu with fish", "FISH");
        foodService.addToMenu(menuItem);

        menuItem = new MenuItem(menuId.getAndIncrement(), "Vegan Menu", 12, "Menu with vegan food", "VEGAN");
        foodService.addToMenu(menuItem);

        menuItem = new MenuItem(menuId.getAndIncrement(), "Vegetarian Menu", 6, "Menu with vegetarian food", "VEGETARIAN");
        foodService.addToMenu(menuItem);
    }

}
