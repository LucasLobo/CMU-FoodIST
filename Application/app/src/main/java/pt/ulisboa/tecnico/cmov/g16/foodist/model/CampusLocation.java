package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.location.Location;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public class CampusLocation extends Location {

    public enum Campus {
        ALAMEDA(R.string.alameda, 38.7352722896, -9.13268566132, true),
        TAGUS(R.string.tagus, 38.7370274, -9.3026572, true),
        UNKNOWN(R.string.unknown_campus, null, null, false);

        public int id;
        public Double latitude;
        public Double longitude;
        public boolean display;

        Campus(int id, Double latitude, Double longitude, Boolean display) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.display = display;
        }
    }

    private Campus campus;
    private String name;

    public CampusLocation() {
        super("");
        this.campus = Campus.UNKNOWN;
    }

    public CampusLocation(String name, Double latitude, Double longitude) {
        super("");
        this.name = name;
        setLocation(latitude, longitude, true);
    }

    public CampusLocation(Campus campus, String name) {
        super("");
        super.setLatitude(campus.latitude);
        super.setLongitude(campus.longitude);
        this.campus = campus;
        this.name = name;
    }


    public void setLocation(Double latitude, Double longitude, Boolean calculateCampus) {
        super.setLatitude(latitude);
        super.setLongitude(longitude);
        if (calculateCampus) {
            float[] distance = {0};
            Location.distanceBetween(latitude, longitude, Campus.ALAMEDA.latitude, Campus.ALAMEDA.longitude, distance);

            float CAMPUS_RADIUS_METERS = 750;
            if (distance[0] < CAMPUS_RADIUS_METERS) {
                campus = Campus.ALAMEDA;
                return;
            }

            Location.distanceBetween(latitude, longitude, Campus.TAGUS.latitude, Campus.TAGUS.longitude, distance);

            if (distance[0] < CAMPUS_RADIUS_METERS) {
                campus = Campus.TAGUS;
                return;
            }

            campus = Campus.UNKNOWN;
        }
    }

    public void setLocation(Location location, Boolean calculateCampus) {
        setLocation(location.getLatitude(), location.getLongitude(), calculateCampus);
    }

    public Campus getCampus() {
        return campus;
    }

    public String getName() {
        return name;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }
}
