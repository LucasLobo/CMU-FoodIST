package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable;

import com.grpc.Contract.Signature;
import com.grpc.Contract.JoinQueueRequest;
import com.grpc.Contract.JoinQueueResponse;
import com.grpc.GrpcServiceGrpc;

import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcRunnable;

import static pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Log.appendLogs;

public abstract class JoinQueueRunnable extends GrpcRunnable<Integer> {

    private Integer foodServiceId;

    protected JoinQueueRunnable(Integer foodServiceId) {
        this.foodServiceId = foodServiceId;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return joinQueue(blockingStub);
    }

    private String joinQueue(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) throws StatusRuntimeException {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** JoinQueue: foodServiceId:''{0}''", foodServiceId);
        JoinQueueRequest request = JoinQueueRequest.newBuilder().setFoodServiceId(foodServiceId).build();
        JoinQueueResponse response = blockingStub.joinQueue(request);

        Signature signature = response.getSignature();

        int userQueueId = response.getUserQueueId();
        setResult(userQueueId);

        appendLogs(
                logs,
                ">>> User queue Id=''{0}''",
                userQueueId
        );

        return logs.toString();
    }
}
