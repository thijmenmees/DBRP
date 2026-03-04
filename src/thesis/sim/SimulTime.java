package thesis.sim;
public class SimulTime {
    private int week;
    private int day;
    private int hour;
    private double hourProgression;
    public SimulTime(int week, int day, int hour) {
        this.week = week;
        this.day = day;
        this.hour = hour;
        this.hourProgression = 0.0;
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public double getHourProgression() {
        return hourProgression;
    }

    public boolean isBefore(SimulTime other) {
        if (this.week > other.getWeek()) {
            return false;
        } else if (this.week < other.getWeek()) {
            return true;
        } else if (this.day > other.getDay()) {
            return false;
        } else if (this.day < other.getDay()) {
            return true;
        } else if (this.hour > other.getHour()) {
            return false;
        } else if (this.hour < other.hour) {
            return true;
        }
        return (this.hourProgression < other.getHourProgression());
    }

    public void progress(int week, int day, int hour) {
        this.hour += hour;
        day += Math.floorDiv(this.hour, 24);
        this.hour %= 24;
        this.day += day;
        week += Math.floorDiv(this.day, 7);
        this.day %= 7;
        this.week += week;
    }

    public void progress(double hours) {
        this.hourProgression += hours;
        int actualhours = (int) this.hourProgression;
        this.hourProgression %= 1;
        progress(0,0,actualhours);
    }

    public void setCustom(double simulatedTime) {
        double correctedTime = simulatedTime * Parameters.progressionX;
        this.week = Math.floorDiv((int) correctedTime, 3600*24*7);
        correctedTime %= 3600*24*7;
        this.day  = Math.floorDiv((int) correctedTime, 3600*24);
        correctedTime %= 3600*24;
        this.hour = Math.floorDiv((int) correctedTime, 3600);
        correctedTime %= 3600;
        this.hourProgression = correctedTime / 3600;
    }

    public double hoursBetween(SimulTime other) {
        double deze = 0.0;
        deze += 7*24* week;
        deze +=   24* day;
        deze +=       hour;
        deze +=       hourProgression;
        double andere = 0.0;
        andere += 7*24* other.week;
        andere +=   24* other.day;
        andere +=       other.hour;
        andere +=       other.hourProgression;
        return deze - andere;
    }

    public SimulTime(SimulTime other) {
        this.week = other.week;
        this.day = other.day;
        this.hour = other.hour;
        this.hourProgression = other.hourProgression;
    }
}
