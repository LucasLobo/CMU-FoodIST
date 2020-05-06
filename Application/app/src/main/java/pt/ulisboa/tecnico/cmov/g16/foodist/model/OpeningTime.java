package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;

public class OpeningTime {

    public static class Schedule {

        private LocalTime openTime;
        private LocalTime closeTime;

        Schedule(LocalTime openTime, LocalTime closeTime) {
            this.openTime = openTime;
            this.closeTime = closeTime;
        }

        Schedule(Integer openingHour, Integer openingMinute, Integer closingHour, Integer closingMinute) {
            this(LocalTime.of(openingHour, openingMinute), LocalTime.of(closingHour, closingMinute));
        }

        public LocalTime getOpenTime() {
            return openTime;
        }

        public LocalTime getCloseTime() {
            return closeTime;
        }

    }

    private HashMap<User.UserStatus, ArrayList<Schedule>> schedules = new HashMap<>();;

    public void addScheduleForStatus(User.UserStatus status, Integer openingHour, Integer openingMinute, Integer closingHour, Integer closingMinute) {
        Schedule schedule = new Schedule(openingHour, openingMinute, closingHour, closingMinute);
        ArrayList<Schedule> statusSchedules = schedules.get(status);
        if (statusSchedules == null) {
            statusSchedules = new ArrayList<>();
            schedules.put(status, statusSchedules);
        }
        statusSchedules.add(schedule);
    }

    Boolean isOpenAt(LocalTime time, User.UserStatus status) {
        ArrayList<Schedule> statusSchedules = schedules.get(status);
        if (statusSchedules == null) return false;
        for (Schedule schedule : statusSchedules) {
            if (time.isAfter(schedule.openTime) && time.isBefore(schedule.closeTime)) {
                return true;
            }
        }
        return false;
    }

    Boolean isOpen(User.UserStatus status) {
        return isOpenAt(LocalTime.now(), status);
    }

    public ArrayList<Schedule> getScheduleList(User.UserStatus status) {
        return schedules.get(status);
    }
}
