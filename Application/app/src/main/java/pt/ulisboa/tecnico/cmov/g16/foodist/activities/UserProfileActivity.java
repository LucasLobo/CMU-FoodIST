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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.CampusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.UserDietaryAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.UserStatusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
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

    TextView selectedCampus;
    TextView selectedStatus;
    TextView userNameView;
    Button loginButton;
    Button statusButton;
    Button addConstraintsButton;



    //----------------------------------------OnCreate----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = (Data) getApplicationContext();
        user = data.getUser();

        final Button campusButton = findViewById(R.id.selectCampus);
        final Switch loc_aut = findViewById(R.id.loc_aut);

        statusButton = findViewById(R.id.selectStatus);
        addConstraintsButton = findViewById(R.id.addConstraints);
        listProfileView = findViewById(R.id.profileView);
        listConstraintsView = findViewById(R.id.constraintsView);
        loginButton = findViewById(R.id.login);
        userNameView = findViewById(R.id.username);

        /*_____________________________________CAMPUS_____________________________________________*/
        if(user.isLocAuto()){
          loc_aut.setChecked(true);
        }

        selectedCampus = findViewById(R.id.campusSelected);
        selectedCampus.setText("Campus: " + getString(user.getCampusResourceId()));

        final CampusAdapter adapterCampus = new CampusAdapter(this);

        campusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listProfileView.setAdapter(adapterCampus);
                listProfileView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CampusLocation.Campus campus = adapterCampus.getItem(position);
                        Toast.makeText(UserProfileActivity.this, "New Campus Selected: " + getString(campus.id), Toast.LENGTH_SHORT).show();
                        listProfileView.setAdapter(null);
                        user.setCampus(campus);

                        user.setLocAuto(false);
                        loc_aut.setChecked(false);

                        Toast.makeText(UserProfileActivity.this, "Location Finder changed to Manual mode", Toast.LENGTH_SHORT).show();
                        if (user.isLoggedIn()) {
                            saveProfile();
                        }
                        selectedCampus.setText("Campus: " + getString(campus.id));
                    }
                });
            }
        });

        loc_aut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    user.setLocAuto(true);
                    Toast.makeText(UserProfileActivity.this, "Location Finder changed to Automatic mode", Toast.LENGTH_SHORT).show();
                }
                else {
                    user.setLocAuto(false);
                    Toast.makeText(UserProfileActivity.this, "Location Finder changed to Manual mode", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        setUpLogin();
        setUpStatus();
        setUpConstraints();
    }

    private void setUpLogin() {
        String username = user.getUsername();
        Log.i(TAG, "setUpLogin: " + username);
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
        final UserDietaryAdapter adapterDietary = new UserDietaryAdapter(this);
        final UserDietaryAdapter adapterCurrentDietary = new UserDietaryAdapter(this, new ArrayList<>(user.getDietaryConstraints()));
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
                            Log.i(TAG, "constraint: added");
                            if (user.isLoggedIn()) {
                                saveProfile();
                                Log.i(TAG, "constraint: profile updated");
                            }
                            Toast.makeText(UserProfileActivity.this, "Added New Dietary Constraint: " + getString(dietary.resourceId), Toast.LENGTH_SHORT).show();
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
                Log.i(TAG, "constraint: removed");
                if (user.isLoggedIn()) {
                    saveProfile();
                    Log.i(TAG, "constraint: profile updated");

                }
                Toast.makeText(UserProfileActivity.this, "Removed Dietary Constraint: " + getString(dietary.resourceId), Toast.LENGTH_SHORT).show();
                adapterCurrentDietary.remove(dietary);
                adapterCurrentDietary.notifyDataSetChanged();
            }

        });
    }

    private void setUpStatus() {
        /*_____________________________________STATUS_____________________________________________*/
        selectedStatus = findViewById(R.id.statusSelected);
        selectedStatus.setText("Status: " + getString(user.getStatusID()));
        final UserStatusAdapter adapterStatus = new UserStatusAdapter(this);

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listProfileView.setAdapter(adapterStatus);
                listProfileView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        User.UserStatus status = adapterStatus.getItem(position);
                        Toast.makeText(UserProfileActivity.this, "New Status Selected: " + getString(status.id), Toast.LENGTH_SHORT).show();
                        user.setStatus(status);

                        Log.i(TAG, "status: changed");
                        if (user.isLoggedIn()) {
                            saveProfile();
                            Log.i(TAG, "status: profile update");
                        }
                        listProfileView.setAdapter(null);
                        selectedStatus.setText("Status: " + getString(status.id));
                    }
                });
            }
        });
    }

    public void saveProfile(){
        new GrpcTask(new SaveProfileRunnable(user.getUsername(), user.getPassword(), user.getStatus(), user.getDietaryConstraints()) {
            @Override
            protected void callback(String result) {
                if (result == null) {
                    Log.i(TAG, "callback: unknown error");
                } else if (result.equals("OK")) {
                    Log.i(TAG, "callback: user saved");
                } else if (result.equals("INCORRECT_PASSWORD") ||
                        result.equals("USERNAME_DOES_NOT_EXIST")) {
                    user.logout();
                    setUpLogin();
                }
            }
        }).execute();
    }
}
