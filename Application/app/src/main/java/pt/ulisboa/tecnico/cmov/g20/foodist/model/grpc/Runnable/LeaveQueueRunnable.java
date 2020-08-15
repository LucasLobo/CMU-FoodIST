package pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.Runnable;

import com.grpc.GrpcServiceGrpc;
import com.grpc.Contract.LeaveQueueRequest;
import com.grpc.Contract.LeaveQueueResponse;

import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.GrpcRunnable;

import static pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.util.Log.appendLogs;

public abstract class LeaveQueueRunnable extends GrpcRunnable<String> {

    private Integer userQueueId;
    private Integer foodServiceId;
    private Integer duration;


    protected LeaveQueueRunnable(Integer userQueueId, Integer foodServiceId, Integer duration) {
        this.userQueueId = userQueueId;
        this.foodServiceId = foodServiceId;
        this.duration = duration;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return leaveQueue(blockingStub);
    }

    private String leaveQueue(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** LeaveQueue: userId: ''{0}'' foodServiceId:''{1}'' duration:''{2}''", userQueueId, foodServiceId, duration);

        LeaveQueueRequest request = LeaveQueueRequest.newBuilder().setUserQueueId(userQueueId).setFoodServiceId(foodServiceId).setDuration(duration).build();
        LeaveQueueResponse response = blockingStub.leaveQueue(request);

        String result = response.getResult();
        setResult(result);

        switch (result) {
            case "OK":
            case "USER_NOT_IN_QUEUE":
            case "FOOD_SERVICE_NOT_FOUND":
                appendLogs(
                        logs,
                        ">>> {0}",
                        result
                );
                break;
            default:
                appendLogs(
                        logs,
                        ">>> {0}: Unknown error code",
                        result
                );
                break;
        }

        return logs.toString();
    }
}
