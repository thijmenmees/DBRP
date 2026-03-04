package thesis.sim;

import java.util.Random;

public class Location {
    public double xCoord;
    public double yCoord;
    public int capacity;
    public int load;
    public int district;

    public Location(double xCoord, double yCoord, int capacity) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.capacity = capacity;
        this.load = 0;
        this.district = -1;
    }

    public Location(double maxX, double maxY, int capacity, Random rand) {
        this.xCoord = rand.nextDouble() * maxX;
        this.yCoord = rand.nextDouble() * maxY;
        this.capacity = capacity;
        this.load = 0;
    }

    public Location(Location other) {
        this.xCoord = other.xCoord;
        this.yCoord = other.yCoord;
        this.capacity = other.capacity;
        this.load = other.load;
        this.district = other.district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public boolean deposit() {
        if (load == capacity) {
            return false;
        }
        load += 1;
        return true;
    }

    public boolean draw() {
        if (load == 0) {
            return false;
        }
        load -= 1;
        return true;
    }
}
