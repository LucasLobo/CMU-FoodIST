package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.FoodConstraintAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.CampusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.UserStatusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.SaveProfileRunnable;


public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private Data data;
    private User user;

    private RecyclerView constraintListView;
    private TextView userNameView;
    private Button loginButton;
    private Spinner campusSpinner;
    private Switch locAut;
    private Spinner statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = (Data) getApplicationContext();
        user = data.getUser();

        campusSpinner = findViewById(R.id.selectCampus);
        locAut = findViewById(R.id.loc_aut);

        statusSpinner = findViewById(R.id.selectStatus);
        constraintListView = findViewById(R.id.profileView);
        loginButton = findViewById(R.id.login);
        userNameView = findViewById(R.id.username);
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

    @Override
    protected void onResume() {
        super.onResume();
        setUpLogin();
        setUpStatus();
        setUpConstraints();
        setUpCampus();
    }

    private void setUpLogin() {
        String username = user.getUsername();
        if (username == null) {
            userNameView.setText(R.string.please_sign_in);
            loginButton.setText(R.string.login_signup);
            /*_______________________________________LOGIN____________________________________________*/
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
            });
        } else {
            loginButton.setText(R.string.logout);
            userNameView.setText(username);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.logout();
                    setUpLogin();
                }
            });
        }
    }

    private void setUpConstraints() {
        final FoodConstraintAdapter adapterDietary = new FoodConstraintAdapter(user);
        constraintListView.setLayoutManager(new LinearLayoutManager(this));
        constraintListView.setHasFixedSize(true);
        constraintListView.setAdapter(adapterDietary);
    }

    private void setUpStatus() {
        statusSpinner.setOnItemSelectedListener(null);
        final UserStatusAdapter adapter = new UserStatusAdapter(this);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setSelection(adapter.getPosition(user.getStatus()), false);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                User.UserStatus status = adapter.getItem(position);
                user.setStatus(status);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing changes
            }
        });
    }

    private void setUpCampus() {
        locAut.setChecked(user.isLocAuto());
        campusSpinner.setOnItemSelectedListener(null);
        final CampusAdapter adapter = new CampusAdapter(this);
        campusSpinner.setAdapter(adapter);
        campusSpinner.setSelection(adapter.getPosition(user.getCampus()), false);
        campusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CampusLocation.Campus campus = adapter.getItem(position);
                user.setCampus(campus);
                user.setLocAuto(false);
                locAut.setChecked(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing changes
            }
        });


        locAut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setLocAuto(isChecked);
            }
        });
    }

    private void saveProfile(){
        new GrpcTask(new SaveProfileRunnable(user.getUsername(), user.getPassword(), user.getStatus(), user.getDietaryConstraints()) {
            @Override
            protected void callback(String result) {
                if (result == null) {
                    Log.e(TAG, "SaveProfileRunnable: unknown error");
                } else if (result.equals("INCORRECT_PASSWORD") ||
                        result.equals("USERNAME_DOES_NOT_EXIST")) {
                    user.logout();
                    setUpLogin();
                }
            }
        }).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (user.isLoggedIn()) saveProfile();
    }
}
