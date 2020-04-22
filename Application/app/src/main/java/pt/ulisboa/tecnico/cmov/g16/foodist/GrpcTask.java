package pt.ulisboa.tecnico.cmov.g16.foodist;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.grpc.Contract.*;
import com.grpc.GrpcServiceGrpc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activityReference;
    private ManagedChannel channel;
    private String host = "10.0.2.2";
    private int port = 50051;


    public GrpcTask(Activity activity) {
        this.activityReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected String doInBackground(String... params) {
        String command = params[0];
        String username = params[1];
        try {
            channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
            GrpcServiceGrpc.GrpcServiceBlockingStub stub = GrpcServiceGrpc.newBlockingStub(channel);

            switch (command) {
                case "register":
                    String password = params[2];
                    RegisterRequest requestRegister = RegisterRequest.newBuilder().setUsername(username).setPassword(password).build();
                    RegisterResponse replyRegister = stub.register(requestRegister);
                    return replyRegister.getResult();

                case "saveProfile":
                    String status = params[2];
                    String constraints = params[3];
                    ProfileRequest requestSave = ProfileRequest.newBuilder().setUsername(username)
                            .setStatus(status).setConstraints(constraints).build();
                    ProfileResponse replySave = stub.saveProfile(requestSave);
                    return replySave.getResult();
                default:
                    return "No such command in the contract";

            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            return String.format("Failed... : %n%s", sw);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Activity activity = activityReference.get();
        if (activity == null) {
            return;
        }
    }
}