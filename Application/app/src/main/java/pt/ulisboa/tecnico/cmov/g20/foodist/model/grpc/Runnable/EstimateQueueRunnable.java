package pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.Runnable;

import com.grpc.Contract.EstimateQueueTimeRequest;
import com.grpc.Contract.EstimateQueueTimeResponse;
import com.grpc.GrpcServiceGrpc;

import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.GrpcRunnable;

import static pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.util.Log.appendLogs;

public abstract class EstimateQueueRunnable extends GrpcRunnable<Integer> {

    private Integer foodServiceId;

    protected EstimateQueueRunnable(Integer foodServiceId) {
        this.foodServiceId = foodServiceId;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return joinQueue(blockingStub);
    }

    private String joinQueue(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) throws StatusRuntimeException {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** EstimateQueueTime: foodSeviceId:''{0}''", foodServiceId);
        EstimateQueueTimeRequest request = EstimateQueueTimeRequest.newBuilder().setFoodServiceId(foodServiceId).build();
        EstimateQueueTimeResponse response = blockingStub.estimateQueueTime(request);

        int time = response.getTime();
        setResult(time);

        if (time == -1) {
            appendLogs(
                    logs,
                    ">>> Could not estimate queue time"
            );
        } else {
            appendLogs(
                    logs,
                    ">>> Estimated time: ''{0}''",
                    time
            );
        }

        return logs.toString();
    }
}
