package pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.Runnable;

import com.grpc.Contract.Profile;
import com.grpc.Contract.Auth;
import com.grpc.Contract.SaveProfileRequest;
import com.grpc.Contract.SaveProfileResponse;
import com.grpc.GrpcServiceGrpc;

import java.util.EnumSet;

import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.User;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.GrpcRunnable;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.util.ModelConverter;

import static pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.util.Log.appendLogs;

public abstract class SaveProfileRunnable extends GrpcRunnable<String> {

    private String username;
    private String password;
    private User.UserStatus status;
    private EnumSet<FoodType> constraints;

    protected SaveProfileRunnable(String username, String password, User.UserStatus status, EnumSet<FoodType> constraints) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.constraints = constraints;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return saveProfile(blockingStub);
    }

    private String saveProfile(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) throws StatusRuntimeException {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** SaveProfile: username=''{0}'' password=''{1}'' status=''{2}'' constraints=''{3}''", username, password, status, constraints);

        Auth auth = Auth.newBuilder().setUsername(username).setPassword(password).build();

        Profile profile = Profile.newBuilder().setStatus(status.name()).addAllConstraints(ModelConverter.FoodTypeSetToList(constraints)).build();

        SaveProfileRequest request = SaveProfileRequest.newBuilder().setAuth(auth).setProfile(profile).build();

        SaveProfileResponse response = blockingStub.saveProfile(request);

        String code = response.getCode();
        setResult(code);
        switch (code) {
            case "OK":
            case "USERNAME_DOES_NOT_EXIST":
            case "INCORRECT_PASSWORD":
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