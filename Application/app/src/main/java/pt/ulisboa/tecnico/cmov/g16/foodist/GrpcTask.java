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

import androidx.appcompat.app.AppCompatActivity;

public class GrpcTask extends AsyncTask<String, Void, String> {
    private final WeakReference<AppCompatActivity> activityReference;
    private ManagedChannel channel;
    private String host = "10.0.2.2";
    private int port = 50051;


    public GrpcTask(AppCompatActivity activity) {
        this.activityReference = new WeakReference<AppCompatActivity>(activity);

    }

    @Override
    protected String doInBackground(String... params) {
        String command = params[0];
        try {
            channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
            GrpcServiceGrpc.GrpcServiceBlockingStub stub = GrpcServiceGrpc.newBlockingStub(channel);

            switch (command) {
                case "register":
                    String username = params[1];
                    String password = params[2];
                    RegisterRequest requestRegister = RegisterRequest.newBuilder().setUsername(username).setPassword(password).build();
                    RegisterResponse replyRegister = stub.register(requestRegister);
                    return replyRegister.getResult();

                case "saveProfile":
                    String profile = params[1];
                    String status = params[2];
                    String constraints = params[3];
                    ProfileRequest requestSave = ProfileRequest.newBuilder().setUsername(profile)
                            .setStatus(status).setConstraints(constraints).build();
                    ProfileResponse replySave = stub.saveProfile(requestSave);
                    return replySave.getResult();

                case "saveMenu":
                    String item = params[1];
                    ItemRequest itemRequest = ItemRequest.newBuilder().setItem(item).build();
                    ItemResponse itemReply = stub.saveMenuItem(itemRequest);
                    return itemReply.getResult();

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
        AppCompatActivity activity = activityReference.get();
        if (activity == null) {
            return;
        }
    }
}