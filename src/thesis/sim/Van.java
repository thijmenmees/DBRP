package thesis.sim;

public class Van extends Location {
    public Van(double xCoord, double yCoord, int capacity) {
        super(xCoord, yCoord, capacity);
    }
    public Van(Van other) {
        super(other);   // Calls Location(Location other)
    }

    public int pickup(int amount) {
        int ret = Math.min(capacity - load, amount);
        load += ret;
        return ret;
    }

    public int dropoff(int amount) {
        int ret = Math.min(load, amount);
        load -= ret;
        return ret;
    }


}
