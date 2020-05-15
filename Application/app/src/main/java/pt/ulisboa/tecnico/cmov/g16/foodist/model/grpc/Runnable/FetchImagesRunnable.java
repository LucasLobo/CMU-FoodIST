package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.protobuf.ByteString;
import com.grpc.Contract.FetchImagesResponse;
import com.grpc.Contract.FetchImagesRequest;
import com.grpc.GrpcServiceGrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcRunnable;

import static pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Log.appendLogs;

public abstract class FetchImagesRunnable extends GrpcRunnable<HashMap<Integer, Bitmap>> {
    private static final String TAG = "FetchImageRunnable";

    private Set<Integer> imageIds;

    public FetchImagesRunnable(Set<Integer> imageIds) {
        this.imageIds = imageIds;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return fetchImage(blockingStub);
    }

    private String fetchImage(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** FetchImage");

        FetchImagesRequest request = FetchImagesRequest.newBuilder().addAllImageId(imageIds).build();
        Iterator<FetchImagesResponse> responseIterator = blockingStub.fetchImages(request);

        HashMap<Integer, ArrayList<ByteString>> imagesByteStringArrays = new HashMap<>();

        while (responseIterator.hasNext()) {
            FetchImagesResponse response = responseIterator.next();

            ByteString chunk = response.getChunk().getData();
            int imageId = response.getChunk().getImageId();
            int position = response.getChunk().getPosition();

            ArrayList<ByteString> imageByteStringArray = imagesByteStringArrays.get(imageId);
            if (imageByteStringArray == null) {
                imageByteStringArray = new ArrayList<>();
                imagesByteStringArrays.put(imageId, imageByteStringArray);
            }
            imageByteStringArray.add(position, chunk);
        }

        HashMap<Integer, Bitmap> bitmaps = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<ByteString>> imageByteStringArrayEntrySet : imagesByteStringArrays.entrySet()) {

            Integer imageId = imageByteStringArrayEntrySet.getKey();
            ByteString imageBytestring = ByteString.EMPTY;
            for (ByteString bytestring : imageByteStringArrayEntrySet.getValue()) {
                imageBytestring = imageBytestring.concat(bytestring);
            }

            byte[] imageBytes = imageBytestring.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            bitmaps.put(imageId, bitmap);
        }

        setResult(bitmaps);

        return logs.toString();
    }
}
