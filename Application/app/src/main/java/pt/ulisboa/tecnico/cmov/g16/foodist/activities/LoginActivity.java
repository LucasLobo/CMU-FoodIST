package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.FetchProfileRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.RegisterRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.SaveProfileRunnable;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    Data data;
    User user;

    EditText username;
    EditText password;
    TextView error;

    String usernameString;
    String passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (Data) getApplicationContext();
        user = data.getUser();

        final Button signInButton = findViewById(R.id.signIn);
        final Button createAccount = findViewById(R.id.signUp);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        error = findViewById(R.id.login_activity_error);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();

                new GrpcTask(new RegisterRunnable(usernameString, passwordString) {
                    @Override
                    protected void callback(String result) {
                        if (result.equals("USERNAME_TAKEN")) {
                            error.setText(R.string.username_already_taken);
                            error.setVisibility(View.VISIBLE);
                        } else if (result.equals("OK")) {
                            user.login(usernameString, passwordString);
                            new GrpcTask(new SaveProfileRunnable(usernameString, passwordString, user.getStatus(), user.getDietaryConstraints()) {
                                @Override
                                protected void callback(String result) {
                                    if (result.equals("OK")) {
                                        LoginActivity.this.finish();
                                    } else if (result.equals("INCORRECT_PASSWORD") ||
                                               result.equals("USERNAME_DOES_NOT_EXIST")) {
                                        error.setVisibility(View.VISIBLE);
                                        error.setText(R.string.incorrect_credentials);
                                    } else {
                                        error.setVisibility(View.VISIBLE);
                                        error.setText(getString(R.string.unknown_error_code, result));
                                    }
                                }
                            }).execute();

                        } else {
                            error.setVisibility(View.VISIBLE);
                            error.setText(getString(R.string.unknown_error_code, result));
                        }
                    }
                }).execute();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();

                new GrpcTask(new FetchProfileRunnable(usernameString, passwordString) {
                    @Override
                    protected void callback(FetchProfileResult result) {
                        if (result == null) {
                            error.setVisibility(View.VISIBLE);
                            error.setText(getString(R.string.unknown_error));
                        } else if (result.getCode().equals("OK")) {
                            user.login(usernameString, passwordString);
                            user.setStatus(result.getStatus());
                            user.setDietaryConstraints(result.getDietaryConstraints());
                            LoginActivity.this.finish();
                        } else if (result.getCode().equals("USERNAME_DOES_NOT_EXIST") ||
                                 result.getCode().equals("INCORRECT_PASSWORD")) {

                            error.setVisibility(View.VISIBLE);
                            error.setText(R.string.incorrect_credentials);

                        } else {
                            error.setVisibility(View.VISIBLE);
                            error.setText(getString(R.string.unknown_error_code, result));
                        }
                    }
                }).execute();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void checkFields() {
        error.setVisibility(View.INVISIBLE);

        usernameString = username.getText().toString().trim();
        passwordString = password.getText().toString().trim();

        if (usernameString.isEmpty() || passwordString.isEmpty()) {
            error.setText(R.string.username_password_missing);
            error.setVisibility(View.VISIBLE);
        }
    }
}
