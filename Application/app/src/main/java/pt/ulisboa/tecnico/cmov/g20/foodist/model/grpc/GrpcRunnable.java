package pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc;

import com.grpc.GrpcServiceGrpc;

public abstract class GrpcRunnable<T> {
    private T result;

    void callback() {
        callback(result);
    }

    /** Perform a grpcRunnable and return all the logs. */
    abstract protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub);
    abstract protected void callback(T result);

    public void setResult(T result) {
        this.result = result;
    }
}