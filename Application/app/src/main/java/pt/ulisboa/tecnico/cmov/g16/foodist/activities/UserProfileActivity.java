package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
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
        final Button dietaryButton = findViewById(R.id.dietary);
        final Button addConstraintsButton = findViewById(R.id.addContraints);
        final ListView listStatusView = findViewById(R.id.statusView);
        final ListView listCampusView = findViewById(R.id.campusView);
        final ListView listDietaryConstraintsView = findViewById(R.id.dietaryConstraints);


        /*_____________________________________CAMPUS_____________________________________________*/

        selectedCampus = findViewById(R.id.campusSelected);
        selectedCampus.setText("Your Campus: " + getString(user.getCampusResourceId()));

        final CampusAdapter adapterCampus = new CampusAdapter(this);

        campusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listStatusView.setAdapter(null);
                listDietaryConstraintsView.setAdapter(null);
                listCampusView.setAdapter(adapterCampus);
                listCampusView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CampusLocation.Campus campus = adapterCampus.getItem(position);
                        Toast.makeText(UserProfileActivity.this, "New Campus Selected: " + getString(campus.id), Toast.LENGTH_SHORT).show();
                        listCampusView.setAdapter(null);
                        user.setCampus(campus);
                        selectedCampus.setText("Your Campus: " + getString(campus.id));
                    }
                });
            }
        });

        /*_____________________________________STATUS_____________________________________________*/
        selectedStatus = findViewById(R.id.statusSelected);
        selectedStatus.setText("Please select a Status.");
        final UserStatusAdapter adapterStatus = new UserStatusAdapter(this);

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listCampusView.setAdapter(null);
                listDietaryConstraintsView.setAdapter(null);
                listStatusView.setAdapter(adapterStatus);
                listStatusView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        User.UserStatus status = adapterStatus.getItem(position);
                        Toast.makeText(UserProfileActivity.this, "New Status Selected: " + getString(status.id), Toast.LENGTH_SHORT).show();
                        user.setStatus(status);
                        listStatusView.setAdapter(null);
                        selectedStatus.setText("Your Status: " + getString(status.id));
                    }
                });
            }
        });

        /*_____________________________________DIETARY____________________________________________*/
        final UserDietaryAdapter adapterDietary = new UserDietaryAdapter(this);

        addConstraintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listStatusView.setAdapter(null);
                listCampusView.setAdapter(null);
                listDietaryConstraintsView.setAdapter(adapterDietary);
                listDietaryConstraintsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        User.UserDietary dietary = adapterDietary.getItem(position);

                        if (!user.getDietaryConstraints().contains(dietary)) {
                            user.addDietaryConstraints(dietary);
                            Toast.makeText(UserProfileActivity.this, "Added New Dietary Constraint: " + getString(dietary.id), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(UserProfileActivity.this, "You already have this constraint in your dietary.", Toast.LENGTH_SHORT).show();
                        }
                        listDietaryConstraintsView.setAdapter(null);
                    }
                });
            }
        });

        final UserDietaryAdapter adapterCurrentDietary = new UserDietaryAdapter(this, user.getDietaryConstraints());

        dietaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listStatusView.setAdapter(null);
                listCampusView.setAdapter(null);
                if(user.getDietaryConstraints().isEmpty()){
                    listDietaryConstraintsView.setAdapter(null);
                    Toast.makeText(UserProfileActivity.this, "You currently don't have any dietary constraints.", Toast.LENGTH_SHORT).show();
                }
                else {
                    listDietaryConstraintsView.setAdapter(adapterCurrentDietary);
                    listDietaryConstraintsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            listDietaryConstraintsView.setAdapter(null);
                        }
                    });
                }
            }
        });
    }
}
