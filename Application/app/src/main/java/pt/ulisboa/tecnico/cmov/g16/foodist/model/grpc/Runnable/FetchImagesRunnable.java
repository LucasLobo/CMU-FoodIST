package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.protobuf.ByteString;
import com.grpc.Contract.ImageMetaData;
import com.grpc.Contract.FetchImagesFromMenuResponse;
import com.grpc.Contract.FetchImagesFromMenuRequest;
import com.grpc.GrpcServiceGrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcRunnable;

import static pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Log.appendLogs;

public abstract class FetchImagesRunnable extends GrpcRunnable<ArrayList<Bitmap>> {
    private static final String TAG = "FetchImageRunnable";

    private Integer foodServiceId;
    private Integer menuItemId;

    public FetchImagesRunnable(Integer foodServiceId, Integer menuItemId) {
        this.foodServiceId = foodServiceId;
        this.menuItemId = menuItemId;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return fetchImage(blockingStub);
    }

    private String fetchImage(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** FetchImage");

        ImageMetaData metaData = ImageMetaData.newBuilder().setFoodServiceId(foodServiceId).setMenuItemId(menuItemId).build();
        FetchImagesFromMenuRequest request = FetchImagesFromMenuRequest.newBuilder().setMetadata(metaData).build();

        Iterator<FetchImagesFromMenuResponse> responseIterator = blockingStub.fetchImages(request);

        HashMap<Integer, ArrayList<ByteString>> imagesByteStringArrays = new HashMap<>();

        while (responseIterator.hasNext()) {
            FetchImagesFromMenuResponse response = responseIterator.next();

            ByteString chunk = response.getChunk().getData();
            int imageIndex = response.getChunk().getImageIndex();
            int position = response.getChunk().getPosition();

            ArrayList<ByteString> imageByteStringArray = imagesByteStringArrays.get(imageIndex);
            if (imageByteStringArray == null) {
                imageByteStringArray = new ArrayList<>();
                imagesByteStringArrays.put(imageIndex, imageByteStringArray);
            }
            imageByteStringArray.add(position, chunk);
        }

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (ArrayList<ByteString> imageByteStringArray : imagesByteStringArrays.values()) {
            ByteString imageBytestring = ByteString.EMPTY;
            for (ByteString bytestring : imageByteStringArray) {
                imageBytestring = imageBytestring.concat(bytestring);
            }

            byte[] imageBytes = imageBytestring.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            bitmaps.add(bitmap);

        }

        setResult(bitmaps);

        return logs.toString();
    }
}
