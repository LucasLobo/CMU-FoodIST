package com.grpc.server;

import com.google.protobuf.ByteString;
import com.grpc.Contract;
import com.grpc.Contract.*;
import com.grpc.GrpcServiceGrpc;

import com.grpc.server.model.MenuItem;
import com.grpc.server.model.FoodService;
import com.grpc.server.model.User;
import com.grpc.server.model.UserNotInQueueException;
import com.grpc.server.util.Chunks;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase {

	private enum AuthMessage {
		OK, USERNAME_TAKEN, USERNAME_DOES_NOT_EXIST, INCORRECT_PASSWORD
	}

    public static GrpcServiceImpl instance = null;
    private final HashMap<String, User> users = new HashMap<>(); //username, User
    private final HashMap<Integer, FoodService> foodServices = new HashMap<>();
    private final HashMap<Integer, ByteString> images = new HashMap<>();

    private final AtomicInteger userQueueId = new AtomicInteger(0);
    private final AtomicInteger menuId = new AtomicInteger(0);
    private final AtomicInteger imageId = new AtomicInteger(0);

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
        System.out.print(Instant.now().toString() + "\n*** Register Request Received\n" + request);

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
        System.out.println(">>> " + code + "\n");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveProfile(SaveProfileRequest request, StreamObserver<SaveProfileResponse> responseObserver) {
        System.out.print(Instant.now().toString() + "\n*** Save Profile Request Received\n" + request);

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
        System.out.println(">>> " + code + "\n");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void fetchProfile(FetchProfileRequest request, StreamObserver<FetchProfileResponse> responseObserver) {
        System.out.print(Instant.now().toString() + "\n*** Fetch Profile Request Received:\n" + request);

        AuthMessage authMessage = auth(request.getAuth());
		String username = request.getAuth().getUsername().toLowerCase();
        User user = users.get(username);
        Profile.Builder profileBuilder = Profile.newBuilder();

        String code = authMessage.name();
        if (authMessage.equals(AuthMessage.OK)) {
            List<String> constraints = user.getConstraints();
            profileBuilder.addAllConstraints(constraints);
            profileBuilder.setStatus(user.getStatus());
            System.out.println(">>> " + user.getStatus());
            System.out.println(">>> " + user.getConstraints());
        }
        System.out.println(">>> " + code + "\n");
        FetchProfileResponse response = FetchProfileResponse.newBuilder()
                .setCode(code)
                .setProfile(profileBuilder.build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void saveMenuItem(SaveMenuItemRequest request, StreamObserver<SaveMenuItemResponse> responseObserver) {
        System.out.print(Instant.now().toString() + "\n*** Save Menu Request Received\n" + request);

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
            System.out.println(">>> " + menuItem.getId());
            code = "OK";
        } catch (NullPointerException e) {
            code = "FOOD_SERVICE_NOT_FOUND";
        }

        SaveMenuItemResponse response = responseBuilder.setCode(code).build();

        System.out.println(">>> " + code + "\n");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<SaveImageToMenuItemRequest> saveImage(StreamObserver<SaveImageToMenuItemResponse> responseObserver) {
        System.out.println(Instant.now().toString() + "\n*** Save Image Request Received");
        return new StreamObserver<SaveImageToMenuItemRequest>() {
            Integer foodServiceId;
            Integer menuItemId;
            final Integer imageId = GrpcServiceImpl.this.imageId.getAndIncrement();
            final ArrayList<ByteString> image = new ArrayList<>();

            @Override
            public void onNext(SaveImageToMenuItemRequest request) {
                if (request.hasMetaData()) {
                    foodServiceId = request.getMetaData().getFoodServiceId();
                    menuItemId = request.getMetaData().getMenuItemId();
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
                System.out.println(">>> OK\n");
                FoodService foodService = foodServices.get(foodServiceId);
                foodService.addImageToMenu(menuItemId, imageId);
                images.put(imageId, imageBytestring);
                SaveImageToMenuItemResponse response = SaveImageToMenuItemResponse.newBuilder().setCode("OK").setImageId(imageId).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void fetchImages(FetchImagesRequest request, StreamObserver<FetchImagesResponse> responseObserver) {
        System.out.println(Instant.now().toString() + "\n*** Fetch Image Request Received:");

        List<Integer> imageIds = request.getImageIdList();
        for (Integer imageId : imageIds) {
            ByteString image = images.get(imageId);
            byte[] byteArray = image.toByteArray();
            byte[][] chunkedByteArrays = Chunks.splitArray(byteArray, 1024);
            int numberOfChunks = chunkedByteArrays.length;

            FetchImagesResponse response;
            for (int i = 0; i < numberOfChunks; i++) {
                ImageChunk chunk = ImageChunk.newBuilder().setImageId(imageId).setPosition(i).setData(ByteString.copyFrom(chunkedByteArrays[i])).build();
                response = FetchImagesResponse.newBuilder().setChunk(chunk).build();
                responseObserver.onNext(response);
            }
        }
        System.out.println(">>> " + imageIds.size() + " image(s) fetched\n");
        responseObserver.onCompleted();
    }

    @Override
    public void fetchMenus(FetchMenusRequest request, StreamObserver<FetchMenusResponse> responseObserver) {
//        System.out.print(Instant.now().toString() + "\n*** Fetch Menu Request Received:\n" + request);

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
                        .addAllImageId(menuItem.getImageIds()).build();

                responseBuilder.addMenuItems(item);
            }
//            System.out.println(">>> OK\n");
        } catch (NullPointerException ignored) {
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void joinQueue(JoinQueueRequest request, StreamObserver<JoinQueueResponse> responseObserver) {
        System.out.print(Instant.now().toString() + "\n*** Join Queue Request Received:\n" + request);

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
        System.out.println(">>> UserQueueId: " + userQueueId + "\n");
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void leaveQueue(LeaveQueueRequest request, StreamObserver<LeaveQueueResponse> responseObserver) {
        System.out.print(Instant.now().toString() + "\n*** Leave Queue Request Received:\n" + request);

        int duration = request.getDuration();
        int foodServiceId = request.getFoodServiceId();
        int userQueueId = request.getUserQueueId();

        String code;
        FoodService foodService = foodServices.get(foodServiceId);
        LeaveQueueResponse response;
        try {
            foodService.removeFromQueue(userQueueId, duration);
            code = "OK";
        } catch (UserNotInQueueException e) {
            code = "USER_NOT_IN_QUEUE";
        } catch (NullPointerException e) {
            code = "FOOD_SERVICE_NOT_FOUND";
        }

        response = LeaveQueueResponse.newBuilder().setResult(code).build();
        System.out.println(">>> " + code + "\n");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void estimateQueueTime(EstimateQueueTimeRequest request, StreamObserver<EstimateQueueTimeResponse> responseObserver) {
//       System.out.print(Instant.now().toString() + "\n*** Estimate Queue Request Received:\n" + request);

        int foodServiceId = request.getFoodServiceId();

        FoodService foodService = foodServices.get(foodServiceId);
        int queueTime;
        try {
            queueTime = (int) Math.round(foodService.estimateQueueTime());
        } catch (NullPointerException e) {
            queueTime = -1;
        }

//        System.out.println(">>> QueueTime: " + queueTime + "\n");
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
