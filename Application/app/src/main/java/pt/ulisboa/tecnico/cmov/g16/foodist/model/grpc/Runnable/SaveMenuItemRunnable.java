package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable;

import com.grpc.Contract;
import com.grpc.Contract.SaveMenuItemResponse;
import com.grpc.Contract.SaveMenuItemRequest;
import com.grpc.GrpcServiceGrpc;


import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcRunnable;

import static pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Log.appendLogs;

public abstract class SaveMenuItemRunnable extends GrpcRunnable<String> {

    private MenuItem item;
    private Integer foodServiceId;

    protected SaveMenuItemRunnable(MenuItem item, Integer foodServiceId) {
        this.item = item;
        this.foodServiceId = foodServiceId;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return saveMenuItem(blockingStub);
    }

    private String saveMenuItem(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) throws StatusRuntimeException {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** SaveMenuItem: foodServiceId=''{0}'' menuItem=''{1}''", foodServiceId, item);


        Contract.MenuItem menuItem = Contract.MenuItem.newBuilder()
                .setName(item.getName())
                .setDescription(item.getDescription())
                .setFoodType(item.getFoodType().name())
                .setPrice(item.getPrice())
                .build();

        SaveMenuItemRequest request = SaveMenuItemRequest.newBuilder().setMenuItem(menuItem).setFoodServiceId(foodServiceId).build();

        SaveMenuItemResponse response = blockingStub.saveMenuItem(request);

        String code = response.getCode();
        setResult(code);
        switch (code) {
            case "OK":
            case "FOOD_SERVICE_NOT_FOUND":
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

        return logs.toString();
    }
}