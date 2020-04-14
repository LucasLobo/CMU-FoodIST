package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import org.threeten.bp.LocalTime;
import java.util.HashMap;

public class OpeningTime {

    public static class Schedule {

        private LocalTime openTime;
        private LocalTime closeTime;

        public Schedule(LocalTime openTime, LocalTime closeTime) {
            this.openTime = openTime;
            this.closeTime = closeTime;
        }

        public LocalTime getOpenTime() {
            return openTime;
        }

        public LocalTime getCloseTime() {
            return closeTime;
        }

    }

    private HashMap<User.UserStatus, Schedule> schedules = new HashMap<>();;

    public void addScheduleStatus(User.UserStatus status, Schedule schedule) {
        schedules.put(status, schedule);
    }

    public Boolean isOpenAt(LocalTime time, User.UserStatus status) {
        Schedule schedule = schedules.get(status);
        if (schedule == null) return false;
        return time.isAfter(schedule.openTime) && time.isBefore(schedule.closeTime);

    }

    public Boolean isOpen(User.UserStatus status) {
        return isOpenAt(LocalTime.now(), status);
    }
}
