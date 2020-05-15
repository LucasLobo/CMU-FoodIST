package pt.ulisboa.tecnico.cmov.g20.foodist.model;

import android.location.Location;

import pt.ulisboa.tecnico.cmov.g20.foodist.R;

public class CampusLocation extends Location {

    public enum Campus {
        ALAMEDA(R.string.alameda, 38.7352722896, -9.13268566132, true),
        TAGUS(R.string.tagus, 38.7370274, -9.3026572, true),
        CTN(R.string.CTN, 38.8119, -9.0942, true),
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

    public CampusLocation(Double latitude, Double longitude) {
        super("");
        setLocation(latitude, longitude, true);
    }

    public void setLocation(Double latitude, Double longitude, Boolean calculateCampus) {
        super.setLatitude(latitude);
        super.setLongitude(longitude);

        if (calculateCampus) {
            float[] distance = {0};
            float CAMPUS_RADIUS_METERS = 750;

            for (Campus campus : Campus.values()) {
                if (!campus.display) continue;

                Location.distanceBetween(latitude, longitude, campus.latitude, campus.longitude, distance);

                if (distance[0] < CAMPUS_RADIUS_METERS) {
                    this.campus = campus;
                    return;
                }
            }
            this.campus = Campus.UNKNOWN;
        }
    }

    public void setLocation(Location location, Boolean calculateCampus) {
        setLocation(location.getLatitude(), location.getLongitude(), calculateCampus);
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }
}
