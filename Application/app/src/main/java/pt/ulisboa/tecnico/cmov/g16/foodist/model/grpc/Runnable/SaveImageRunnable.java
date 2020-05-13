package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.grpc.Contract.ImageChunk;
import com.grpc.Contract.ImageMetaData;
import com.grpc.Contract.SaveImageToMenuItemRequest;
import com.grpc.Contract.SaveImageToMenuItemResponse;
import com.grpc.GrpcServiceGrpc;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Chunks;

import static pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Log.appendLogs;

public abstract class SaveImageRunnable extends GrpcRunnable<Integer> {

    private static final String TAG = "SaveImageRunnable";

    private Bitmap bitmap;
    private Integer foodServiceId;
    private Integer menuItemId;

    protected SaveImageRunnable(Integer foodServiceId, Integer menuItemId, Bitmap bitmap) {
        this.foodServiceId = foodServiceId;
        this.bitmap = bitmap;
        this.menuItemId = menuItemId;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return saveImage(asyncStub);
    }

    private String saveImage(GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        final StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** saveImage: foodServiceId=''{0}''", foodServiceId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

        byte[] imageBytes = baos.toByteArray();

        final CountDownLatch finishLatch = new CountDownLatch(1);

        // Handle Response
        StreamObserver<SaveImageToMenuItemResponse> responseStreamObserver = new StreamObserver<SaveImageToMenuItemResponse>() {
            @Override
            public void onNext(SaveImageToMenuItemResponse response) {
                String code = response.getCode();
                Integer imageId = response.getImageId();
                setResult(imageId);
                switch (code) {
                    case "OK":
                        appendLogs(
                                logs,
                                ">>> {0}",
                                code
                        );
                        break;
                    default:
                        appendLogs(
                                logs,
                                ">>> {0}: Unknown error code",
                                code
                        );
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {

                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };

        // Create Request
        StreamObserver<SaveImageToMenuItemRequest> requestStreamObserver = asyncStub.saveImage(responseStreamObserver);

        try {
            byte[][] chunkedByteArrays = Chunks.splitArray(imageBytes, 1024);
            int numberOfChunks = chunkedByteArrays.length;

            ImageMetaData imageMetaData = ImageMetaData.newBuilder().setFoodServiceId(foodServiceId).setMenuItemId(menuItemId).build();
            SaveImageToMenuItemRequest request = SaveImageToMenuItemRequest.newBuilder().setMetaData(imageMetaData).build();
            requestStreamObserver.onNext(request);

            for (int i = 0; i < numberOfChunks; i++) {
                ByteString chunkBytes = ByteString.copyFrom(chunkedByteArrays[i]);
                ImageChunk chunk = ImageChunk.newBuilder().setPosition(i).setData(chunkBytes).build();
                request = SaveImageToMenuItemRequest.newBuilder().setChunk(chunk).build();
                requestStreamObserver.onNext(request);
            }

            if (finishLatch.getCount() == 0) {
                // RPC completed or errored before we finished sending.
                // Sending further requests won't error, but they will just be thrown away.
                return logs.toString();
            }


        } catch (RuntimeException e) {
            requestStreamObserver.onError(e);
        }

        requestStreamObserver.onCompleted();

        try {
            finishLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
        }

        return logs.toString();
    }



}
