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

    public CampusLocation() {
        super("");
        this.campus = Campus.UNKNOWN;
    }

    public void setLocation(Location location, Boolean calculateCampus) {
        super.set(location);

        if (calculateCampus) {
            float[] distance = {0};
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), Campus.ALAMEDA.latitude, Campus.ALAMEDA.longitude, distance);

            float CAMPUS_RADIUS_METERS = 750;
            if (distance[0] < CAMPUS_RADIUS_METERS) {
                campus = Campus.ALAMEDA;
                return;
            }

            Location.distanceBetween(location.getLatitude(), location.getLongitude(), Campus.TAGUS.latitude, Campus.TAGUS.longitude, distance);

            if (distance[0] < CAMPUS_RADIUS_METERS) {
                campus = Campus.TAGUS;
                return;
            }

            campus = Campus.UNKNOWN;
        }
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }
}
