package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable;

import com.grpc.Contract.Signature;
import com.grpc.Contract.FetchMenusResponse;
import com.grpc.Contract.FetchMenusRequest;
import com.grpc.GrpcServiceGrpc;

import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.ModelConverter;

import static pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Log.appendLogs;

public abstract class FetchMenusRunnable extends GrpcRunnable<List<MenuItem>> {

    private Integer foodServiceId;

    public FetchMenusRunnable(Integer foodServiceId) {
        this.foodServiceId = foodServiceId;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return fetchMenus(blockingStub);
    }

    private String fetchMenus(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** FetchMenus: foodServiceId=''{0}''", foodServiceId);

        FetchMenusRequest request = FetchMenusRequest.newBuilder().setFoodServiceId(foodServiceId).build();
        FetchMenusResponse response = blockingStub.fetchMenus(request);

        List<MenuItem> menuItemsList = ModelConverter.ContractMenuItemsToMenuItems(response.getMenuItemsList());
        setResult(menuItemsList);

        Signature signature = response.getSignature();

        appendLogs(
                logs,
                ">>> {0}",
                menuItemsList
        );

        return logs.toString();
    }
}
