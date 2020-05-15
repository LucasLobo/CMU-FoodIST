package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable;

import com.grpc.Contract.Auth;
import com.grpc.Contract.RegisterRequest;
import com.grpc.Contract.RegisterResponse;
import com.grpc.GrpcServiceGrpc;

import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcRunnable;

import static pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Log.appendLogs;

public abstract class RegisterRunnable extends GrpcRunnable<String> {

    private String username;
    private String password;

    protected RegisterRunnable(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return register(blockingStub);
    }

    private String register(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) throws StatusRuntimeException {

        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** Register: username=''{0}'' password=''{1}''", username, password);


        Auth auth = Auth.newBuilder().setUsername(username).setPassword(password).build();
        RegisterRequest request = RegisterRequest.newBuilder().setAuth(auth).build();
        RegisterResponse response = blockingStub.register(request);

        String code = response.getCode();
        setResult(code);
        switch (code) {
            case "OK":
            case "USERNAME_TAKEN":
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