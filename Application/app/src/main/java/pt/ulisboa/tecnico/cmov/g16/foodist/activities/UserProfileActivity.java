package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.CampusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.UserDietaryAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.UserStatusAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;


public class UserProfileActivity extends Activity {

    Data data;
    User user;

    TextView selectedCampus;
    TextView selectedStatus;

    //----------------------------------------OnCreate----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        data = (Data) getApplicationContext();
        user = data.getUser();

        final Button statusButton = findViewById(R.id.selectStatus);
        final Button campusButton = findViewById(R.id.selectCampus);
        final Button addConstraintsButton = findViewById(R.id.addContraints);
        final ListView listProfileView = findViewById(R.id.profileView);
        final ListView listConstraintsView = findViewById(R.id.constraintsView);
        final Button loginButton = findViewById(R.id.log);
        final Switch loc_aut = findViewById(R.id.loc_aut);
        final TextView userNameView = findViewById(R.id.username);
        userNameView.setText("User: " + user.getUsername());

        /*_____________________________________CAMPUS_____________________________________________*/
        if(user.isLoc_auto()){
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

                        user.setLoc_auto(false);
                        loc_aut.setChecked(false);

                        Toast.makeText(UserProfileActivity.this, "Location Finder changed to Manual mode", Toast.LENGTH_SHORT).show();
                        if(!user.getUsername().equals("NONE")){
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
                    user.setLoc_auto(true);
                    Toast.makeText(UserProfileActivity.this, "Location Finder changed to Automatic mode", Toast.LENGTH_SHORT).show();
                }
                else {
                    user.setLoc_auto(false);
                    Toast.makeText(UserProfileActivity.this, "Location Finder changed to Manual mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                        if(!user.getUsername().equals("NONE")){
                            saveProfile();
                        }
                        listProfileView.setAdapter(null);
                        selectedStatus.setText("Status: " + getString(status.id));
                    }
                });
            }
        });

        /*_____________________________________DIETARY____________________________________________*/
        final UserDietaryAdapter adapterDietary = new UserDietaryAdapter(this);
        final UserDietaryAdapter adapterCurrentDietary = new UserDietaryAdapter(this, user.getDietaryConstraints());

        addConstraintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listProfileView.setAdapter(adapterDietary);
                listProfileView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        User.UserDietary dietary = adapterDietary.getItem(position);

                        if (!user.getDietaryConstraints().contains(dietary)) {
                            user.addDietaryConstraints(dietary);
                            if(!user.getUsername().equals("NONE")){
                                saveProfile();
                            }
                            Toast.makeText(UserProfileActivity.this, "Added New Dietary Constraint: " + getString(dietary.id), Toast.LENGTH_SHORT).show();
                            listConstraintsView.setAdapter(adapterCurrentDietary);
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

                User.UserDietary dietary = adapterCurrentDietary.getItem(position);
                user.removeDietaryConstraints(dietary);
                if(!user.getUsername().equals("NONE")){
                    saveProfile();
                }
                Toast.makeText(UserProfileActivity.this, "Removed Dietary Constraint: " + getString(dietary.id), Toast.LENGTH_SHORT).show();
                listConstraintsView.setAdapter(adapterCurrentDietary);
            }

        });

        /*_______________________________________LOGIN____________________________________________*/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }

    public void saveProfile(){
        new GrpcTask(UserProfileActivity.this).execute("saveProfile", user.getUsername(), user.getStatus().toString(), user.getDietaryConstraints().toString());
    }
}
