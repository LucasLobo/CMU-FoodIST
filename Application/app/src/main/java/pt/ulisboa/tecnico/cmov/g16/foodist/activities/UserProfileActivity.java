package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.CampusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.FoodTypeAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.UserStatusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.SaveProfileRunnable;


public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    Data data;
    User user;

    ListView listProfileView;
    ListView listConstraintsView;

    TextView userNameView;
    Button loginButton;
    Spinner campusSpinner;
    Switch locAut;
    Spinner statusSpinner;
    Button addConstraintsButton;



    //----------------------------------------OnCreate----------------------------------------------
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
        addConstraintsButton = findViewById(R.id.addConstraints);
        listProfileView = findViewById(R.id.profileView);
        listConstraintsView = findViewById(R.id.constraintsView);
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
        /*_____________________________________DIETARY____________________________________________*/
        final FoodTypeAdapter adapterDietary = new FoodTypeAdapter(this);
        final FoodTypeAdapter adapterCurrentDietary = new FoodTypeAdapter(this, new ArrayList<>(user.getDietaryConstraints()));
        listConstraintsView.setAdapter(adapterCurrentDietary);

        addConstraintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listProfileView.setAdapter(adapterDietary);
                listProfileView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        FoodType dietary = adapterDietary.getItem(position);

                        if (!user.getDietaryConstraints().contains(dietary)) {
                            user.addDietaryConstraints(dietary);
                            if (user.isLoggedIn()) {
                                saveProfile();
                            }
                            adapterCurrentDietary.add(dietary);
                            adapterCurrentDietary.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(UserProfileActivity.this, "You already have this constraint in your dietary.", Toast.LENGTH_SHORT).show();
                        }
                        listProfileView.setAdapter(null);
                    }
                });
            }
        });

        listConstraintsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FoodType dietary = adapterCurrentDietary.getItem(position);
                user.removeDietaryConstraints(dietary);
                if (user.isLoggedIn()) {
                    saveProfile();
                }
                adapterCurrentDietary.remove(dietary);
                adapterCurrentDietary.notifyDataSetChanged();
            }

        });
    }

    private void setUpStatus() {
        final UserStatusAdapter adapter = new UserStatusAdapter(this);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setSelection(adapter.getPosition(user.getStatus()), false);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                User.UserStatus status = adapter.getItem(position);
                user.setStatus(status);
                if (user.isLoggedIn()) {
                    saveProfile();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing changes
            }
        });
    }

    private void setUpCampus() {
        locAut.setChecked(user.isLocAuto());
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
}
