package pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.Runnable;

import  com.grpc.Contract.Auth;
import com.grpc.Contract.FetchProfileRequest;
import com.grpc.Contract.FetchProfileResponse;
import com.grpc.GrpcServiceGrpc;

import java.util.EnumSet;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g20.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.User;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.GrpcRunnable;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.util.ModelConverter;

import static pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.util.Log.appendLogs;

public abstract class FetchProfileRunnable extends GrpcRunnable<FetchProfileRunnable.FetchProfileResult> {

    private String username;
    private String password;

    protected FetchProfileRunnable(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected String run(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub, GrpcServiceGrpc.GrpcServiceStub asyncStub) {
        return fetchProfile(blockingStub);
    }

    private String fetchProfile(GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub) {
        StringBuffer logs = new StringBuffer();
        appendLogs(logs, "*** FetchProfile: username:''{0}'' password:''{1}''", username, password);

        Auth auth = Auth.newBuilder().setUsername(username).setPassword(password).build();
        FetchProfileRequest request = FetchProfileRequest.newBuilder().setAuth(auth).build();
        FetchProfileResponse response = blockingStub.fetchProfile(request);

        List<String> constraints = response.getProfile().getConstraintsList();
        String status = response.getProfile().getStatus();
        String code = response.getCode();

        User.UserStatus userStatus = ModelConverter.StringToUserStatus(status);
        EnumSet<FoodType> dietaryConstraints = ModelConverter.StringArrayToFoodTypeSet(constraints);
        setResult(new FetchProfileResult(code, userStatus, dietaryConstraints));

        switch (code) {
            case "OK":
                appendLogs(
                        logs,
                        ">>> {0}: status=''{1}'' constraints=''{2}''",
                        code,
                        status,
                        constraints
                );
                break;
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

    protected static final class FetchProfileResult {
        private String code;
        private User.UserStatus status;
        private EnumSet<FoodType> dietaryConstraints;

        FetchProfileResult(String code, User.UserStatus status, EnumSet<FoodType> dietaryConstraints) {
            this.code = code;
            this.dietaryConstraints = dietaryConstraints;
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public User.UserStatus getStatus() {
            return status;
        }

        public EnumSet<FoodType> getDietaryConstraints() {
            return dietaryConstraints;
        }
    }
}
