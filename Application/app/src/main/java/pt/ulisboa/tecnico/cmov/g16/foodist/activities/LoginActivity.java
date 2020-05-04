package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class LoginActivity extends AppCompatActivity {
    Data data;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (Data) getApplicationContext();
        user = data.getUser();

        final Button signInButton = findViewById(R.id.signIn);
        final TextView username = findViewById(R.id.username);
        final TextView password = findViewById(R.id.password);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GrpcTask(LoginActivity.this).execute("register", username.getText().toString(), password.getText().toString());
                data.getUser().setUsername(username.getText().toString());
                new GrpcTask(LoginActivity.this).execute("saveProfile", user.getUsername(), user.getStatus().toString(), user.getDietaryConstraints().toString());
                finish();
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
}
