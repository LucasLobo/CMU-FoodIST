package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc;

import android.os.AsyncTask;
import android.util.Log;

import com.grpc.GrpcServiceGrpc;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "GrpcTask";

    private final GrpcRunnable grpcRunnable;

    private static final String host = "10.0.2.2";
    private static final int port = 50051;
    private static final ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();


    public GrpcTask(GrpcRunnable grpcRunnable) {
        this.grpcRunnable = grpcRunnable;
    }


    @Override
    protected String doInBackground(Void... nothing) {
        try {
            String logs = grpcRunnable.run(
                    GrpcServiceGrpc.newBlockingStub(channel),
                    GrpcServiceGrpc.newStub(channel));
            return "Success!\n" + logs;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            return "Failed... :\n" + sw;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "onPostExecute: " + result);
        grpcRunnable.callback();
    }

}
